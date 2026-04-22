package org.pabarreiro.barapp.data

import kotlinx.coroutines.flow.*
import org.pabarreiro.barapp.data.local.LocalDataSource
import org.pabarreiro.barapp.data.local.entity.*
import org.pabarreiro.barapp.data.remote.dto.*
import org.pabarreiro.barapp.data.mapper.*
import org.pabarreiro.barapp.data.remote.RemoteDataSource
import org.pabarreiro.barapp.domain.model.*
import org.pabarreiro.barapp.domain.repository.BarRepository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class BarRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val supabaseClient: SupabaseClient
) : BarRepository {

    private val _currentUser = MutableStateFlow<Perfil?>(null)
    override suspend fun getCurrentUser(): Flow<Perfil?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, password: String): Result<Unit> = try {
        println("[BarApp] [Repository] Iniciando login en Supabase para $email")
        
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        
        println("[BarApp] [Repository] Login en Supabase exitoso, recuperando perfil del servidor...")
        val perfil = remoteDataSource.getMe().toDomain()
        _currentUser.value = perfil
        
        println("[BarApp] [Repository] Perfil recuperado: ${perfil.nombre} (${perfil.rol})")
        Result.success(Unit)
    } catch (e: Exception) {
        println("[BarApp] [Repository] Fallo en login en Supabase para $email: ${e.message}")
        Result.failure(e)
    }

    override suspend fun logout() {
        println("[BarApp] [Repository] Cerrando sesión y limpiando Supabase")
        try {
            supabaseClient.auth.signOut()
        } catch (e: Exception) {
            println("[BarApp] [Repository] Error al hacer logout en supabase: ${e.message}")
        }
        _currentUser.value = null
    }

    override fun getMesas(): Flow<List<Mesa>> = localDataSource.getMesas()

    override suspend fun syncMesas(): Result<Unit> = try {
        println("[BarApp] [Repository] Iniciando sincronización de mesas...")
        val remoteMesas = remoteDataSource.getMesas().map { it.toDomain() }
        localDataSource.saveMesas(remoteMesas)
        println("[BarApp] [Repository] Sincronización de mesas completada: ${remoteMesas.size} mesas")
        Result.success(Unit)
    } catch (e: Exception) {
        println("[BarApp] [Repository] Error sincronizando mesas: ${e.message}")
        Result.failure(e)
    }

    override fun getCategorias(): Flow<List<Categoria>> = localDataSource.getCategorias()

    override fun getProductos(categoriaId: Long?): Flow<List<Producto>> = 
        localDataSource.getProductos(categoriaId)

    override suspend fun syncMenu(): Result<Unit> = try {
        println("[BarApp] [Repository] Iniciando sincronización del menú...")
        val remoteCategorias = remoteDataSource.getCategorias().map { it.toDomain() }
        val remoteProductos = remoteDataSource.getProductos(null).map { it.toDomain() }
        localDataSource.saveCategorias(remoteCategorias)
        localDataSource.saveProductos(remoteProductos)
        println("[BarApp] [Repository] Sincronización del menú completada: ${remoteCategorias.size} categorías, ${remoteProductos.size} productos")
        Result.success(Unit)
    } catch (e: Exception) {
        println("[BarApp] [Repository] Error sincronizando menú: ${e.message}")
        Result.failure(e)
    }

    override fun getComandasActivas(mesaId: Long): Flow<List<Comanda>> = 
        localDataSource.getComandasActivas(mesaId)

    override suspend fun createComanda(comanda: Comanda): Result<Comanda> = try {
        println("[BarApp] [Repository] Creando comanda local para mesa ${comanda.mesaId}")

        localDataSource.saveComanda(comanda, isSynced = false)

        try {
            println("[BarApp] [Repository] Intentando sincronizar comanda con el servidor")
            val synced = remoteDataSource.createComanda(comanda.toDTO()).toDomain()
            localDataSource.saveComanda(synced, isSynced = true)
            localDataSource.markAsSynced(synced.id)
            println("[BarApp] [Repository] Comanda sincronizada exitosamente, ID: ${synced.id}")
            Result.success(synced)
        } catch (e: Exception) {
            println("[BarApp] [Repository] No se pudo sincronizar la comanda, se mantiene en local. Error: ${e.message}")
            Result.success(comanda)
        }
    } catch (e: Exception) {
        println("[BarApp] [Repository] Error crítico creando comanda: ${e.message}")
        Result.failure(e)
    }

    override suspend fun addDetalleAComanda(comandaId: String, detalle: DetalleComanda): Result<DetalleComanda> = try {
        localDataSource.saveDetalle(detalle, isSynced = false)
        try {
            val synced = remoteDataSource.addDetalle(comandaId, detalle.toDTO()).toDomain()
            localDataSource.saveDetalle(synced, isSynced = true)
            Result.success(synced)
        } catch (e: Exception) {
            Result.success(detalle)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun pagarComanda(comandaId: String): Result<Unit> = try {
        remoteDataSource.pagareComanda(comandaId)
        // Update local state if needed
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun syncPendingOrders(): Result<Unit> = try {
        val pendingComandas = localDataSource.getPendingComandas()
        if (pendingComandas.isNotEmpty()) {
            println("[BarApp] [Repository] Sincronizando ${pendingComandas.size} comandas pendientes...")
        }
        pendingComandas.forEach { comanda ->
            try {
                val synced = remoteDataSource.createComanda(comanda.toDTO())
                localDataSource.markAsSynced(synced.id ?: "")
                println("[BarApp] [Repository] Comanda ${comanda.id} sincronizada correctamente")
            } catch (e: Exception) {
                println("[BarApp] [Repository] Fallo al sincronizar comanda pendiente ${comanda.id}: ${e.message}")
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        println("[BarApp] [Repository] Error en proceso de sincronización pendiente: ${e.message}")
        Result.failure(e)
    }
}
