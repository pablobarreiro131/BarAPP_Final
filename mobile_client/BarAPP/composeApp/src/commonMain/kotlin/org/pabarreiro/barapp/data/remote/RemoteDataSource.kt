package org.pabarreiro.barapp.data.remote

import org.pabarreiro.barapp.data.remote.dto.*

interface RemoteDataSource {
    suspend fun getMesas(): List<MesaDTO>
    
    suspend fun getCategorias(): List<CategoriaDTO>
    
    suspend fun getProductos(categoriaId: Long?): List<ProductoDTO>
    
    suspend fun getComandasByMesa(mesaId: Long): List<ComandaDTO>
    
    suspend fun createComanda(comanda: ComandaDTO): ComandaDTO
    
    suspend fun addDetalle(comandaId: String, detalle: DetalleComandaDTO): DetalleComandaDTO
    
    suspend fun deleteDetalle(comandaId: String, detalleId: Long)
    
    suspend fun pagareComanda(comandaId: String): ComandaDTO
    
    suspend fun getMe(): PerfilDTO
}

