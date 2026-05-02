package org.pabarreiro.barapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.pabarreiro.barapp.data.local.entity.*

@Dao
interface BarDao {
    // Mesas
    @Query("SELECT * FROM mesas")
    fun getMesas(): Flow<List<MesaEntity>>

    @Query("SELECT * FROM mesas WHERE id = :id")
    suspend fun getMesaById(id: Long): MesaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMesas(mesas: List<MesaEntity>)

    @Query("DELETE FROM mesas")
    suspend fun deleteAllMesas()

    @Transaction
    suspend fun updateMesas(mesas: List<MesaEntity>) {
        deleteAllMesas()
        insertMesas(mesas)
    }

    // Categorias
    @Query("SELECT * FROM categorias")
    fun getCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategorias(categorias: List<CategoriaEntity>)

    @Query("DELETE FROM categorias")
    suspend fun deleteAllCategorias()

    @Transaction
    suspend fun updateCategorias(categorias: List<CategoriaEntity>) {
        deleteAllCategorias()
        insertCategorias(categorias)
    }

    // Productos
    @Query("SELECT * FROM productos WHERE categoriaId = :categoriaId OR :categoriaId IS NULL")
    fun getProductos(categoriaId: Long?): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductos(productos: List<ProductoEntity>)

    @Query("DELETE FROM productos")
    suspend fun deleteAllProductos()

    @Transaction
    suspend fun updateProductos(productos: List<ProductoEntity>) {
        deleteAllProductos()
        insertProductos(productos)
    }

    // Comandas
    @Query("SELECT * FROM comandas WHERE mesaId = :mesaId AND estadoPago = 0")
    fun getComandaActivaByMesa(mesaId: Long): Flow<List<ComandaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComanda(comanda: ComandaEntity)

    @Query("DELETE FROM comandas WHERE mesaId = :mesaId")
    suspend fun deleteComandasByMesa(mesaId: Long)

    @Query("SELECT * FROM comandas WHERE isSynced = 0")
    suspend fun getPendingComandas(): List<ComandaEntity>

    // Detalles
    @Query("SELECT * FROM detalles_comanda WHERE comandaId = :comandaId")
    fun getDetallesByComanda(comandaId: String): Flow<List<DetalleComandaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalle(detalle: DetalleComandaEntity)

    @Query("DELETE FROM detalles_comanda WHERE id = :detalleId")
    suspend fun deleteDetalleById(detalleId: Long)
    
    @Query("SELECT * FROM detalles_comanda WHERE isSynced = 0")
    suspend fun getPendingDetalles(): List<DetalleComandaEntity>

    @Transaction
    suspend fun markAsSynced(comandaId: String) {
        updateComandaSyncStatus(comandaId, true)
        updateDetallesSyncStatus(comandaId, true)
    }

    @Query("UPDATE comandas SET isSynced = :status WHERE id = :id")
    suspend fun updateComandaSyncStatus(id: String, status: Boolean)

    @Query("UPDATE detalles_comanda SET isSynced = :status WHERE comandaId = :comandaId")
    suspend fun updateDetallesSyncStatus(comandaId: String, status: Boolean)
}
