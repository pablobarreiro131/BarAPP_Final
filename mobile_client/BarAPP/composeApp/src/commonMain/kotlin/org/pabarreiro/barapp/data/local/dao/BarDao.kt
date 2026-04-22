package org.pabarreiro.barapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.pabarreiro.barapp.data.local.entity.*

@Dao
interface BarDao {
    // Mesas
    @Query("SELECT * FROM mesas")
    fun getMesas(): Flow<List<MesaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMesas(mesas: List<MesaEntity>)

    // Categorias
    @Query("SELECT * FROM categorias")
    fun getCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategorias(categorias: List<CategoriaEntity>)

    // Productos
    @Query("SELECT * FROM productos WHERE categoriaId = :categoriaId OR :categoriaId IS NULL")
    fun getProductos(categoriaId: Long?): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductos(productos: List<ProductoEntity>)

    // Comandas
    @Query("SELECT * FROM comandas WHERE mesaId = :mesaId AND estadoPago = 0")
    fun getComandaActivaByMesa(mesaId: Long): Flow<List<ComandaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComanda(comanda: ComandaEntity)

    @Query("SELECT * FROM comandas WHERE isSynced = 0")
    suspend fun getPendingComandas(): List<ComandaEntity>

    // Detalles
    @Query("SELECT * FROM detalles_comanda WHERE comandaId = :comandaId")
    fun getDetallesByComanda(comandaId: String): Flow<List<DetalleComandaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetalle(detalle: DetalleComandaEntity)
    
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
