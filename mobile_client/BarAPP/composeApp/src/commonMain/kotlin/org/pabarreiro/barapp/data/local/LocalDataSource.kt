package org.pabarreiro.barapp.data.local

import kotlinx.coroutines.flow.Flow
import org.pabarreiro.barapp.domain.model.*

interface LocalDataSource {
    fun getMesas(): Flow<List<Mesa>>
    suspend fun saveMesas(mesas: List<Mesa>)
    suspend fun getMesaById(id: Long): Mesa?

    fun getCategorias(): Flow<List<Categoria>>
    suspend fun saveCategorias(categorias: List<Categoria>)

    fun getProductos(categoriaId: Long?): Flow<List<Producto>>
    suspend fun saveProductos(productos: List<Producto>)

    fun getComandasActivas(mesaId: Long): Flow<List<Comanda>>
    suspend fun saveComanda(comanda: Comanda, isSynced: Boolean)
    suspend fun deleteComandasByMesa(mesaId: Long)
    suspend fun getPendingComandas(): List<Comanda>
    
    suspend fun saveDetalle(detalle: DetalleComanda, isSynced: Boolean)
    suspend fun deleteDetalle(detalleId: Long)
    suspend fun getPendingDetalles(): List<DetalleComanda>
    
    suspend fun markAsSynced(comandaId: String)
}
