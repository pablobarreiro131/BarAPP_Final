package org.pabarreiro.barapp.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.pabarreiro.barapp.data.local.dao.BarDao
import org.pabarreiro.barapp.data.mapper.toDomain
import org.pabarreiro.barapp.data.mapper.toEntity
import org.pabarreiro.barapp.domain.model.*

class LocalDataSourceImpl(private val barDao: BarDao) : LocalDataSource {
    
    override fun getMesas(): Flow<List<Mesa>> = 
        barDao.getMesas().map { list -> list.map { it.toDomain() } }

    override suspend fun saveMesas(mesas: List<Mesa>) {
        barDao.insertMesas(mesas.map { it.toEntity() })
    }

    override fun getCategorias(): Flow<List<Categoria>> = 
        barDao.getCategorias().map { list -> list.map { it.toDomain() } }

    override suspend fun saveCategorias(categorias: List<Categoria>) {
        barDao.insertCategorias(categorias.map { it.toEntity() })
    }

    override fun getProductos(categoriaId: Long?): Flow<List<Producto>> = 
        barDao.getProductos(categoriaId).map { list -> list.map { it.toDomain() } }

    override suspend fun saveProductos(productos: List<Producto>) {
        barDao.insertProductos(productos.map { it.toEntity() })
    }

    override fun getComandasActivas(mesaId: Long): Flow<List<Comanda>> {
        return barDao.getComandaActivaByMesa(mesaId).flatMapLatest { comandaEntities ->

            kotlinx.coroutines.flow.flowOf(comandaEntities.map { it.toDomain() })
        }
    }

    override suspend fun saveComanda(comanda: Comanda, isSynced: Boolean) {
        barDao.insertComanda(comanda.toEntity(isSynced))
    }

    override suspend fun getPendingComandas(): List<Comanda> =
        barDao.getPendingComandas().map { it.toDomain() }

    override suspend fun saveDetalle(detalle: DetalleComanda, isSynced: Boolean) {
        barDao.insertDetalle(detalle.toEntity(isSynced))
    }

    override suspend fun getPendingDetalles(): List<DetalleComanda> =
        barDao.getPendingDetalles().map { it.toDomain() }

    override suspend fun markAsSynced(comandaId: String) {
        barDao.markAsSynced(comandaId)
    }
}
