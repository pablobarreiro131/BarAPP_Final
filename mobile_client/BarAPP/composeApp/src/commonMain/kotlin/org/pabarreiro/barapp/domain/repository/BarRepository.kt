package org.pabarreiro.barapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.pabarreiro.barapp.domain.model.*

interface BarRepository {
    // Auth
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun getCurrentUser(): Flow<Perfil?>
    suspend fun logout()
    suspend fun hasSession(): Boolean

    // Mesas
    fun getMesas(): Flow<List<Mesa>>
    suspend fun syncMesas(): Result<Unit>

    // Productos & Categorias
    fun getCategorias(): Flow<List<Categoria>>
    fun getProductos(categoriaId: Long?): Flow<List<Producto>>
    suspend fun syncMenu(): Result<Unit>

    // Comandas
    fun getComandasActivas(mesaId: Long): Flow<List<Comanda>>
    suspend fun syncComandasMesa(mesaId: Long): Result<Unit>
    suspend fun createComanda(comanda: Comanda): Result<Comanda>
    suspend fun addDetalleAComanda(comandaId: String, detalle: DetalleComanda): Result<DetalleComanda>
    suspend fun deleteDetalleFromComanda(comandaId: String, detalleId: Long, mesaId: Long): Result<Unit>
    suspend fun pagarComanda(comandaId: String): Result<Unit>
    suspend fun getMesa(mesaId: Long): Mesa?
    
    // Sync
    suspend fun syncPendingOrders(): Result<Unit>
}
