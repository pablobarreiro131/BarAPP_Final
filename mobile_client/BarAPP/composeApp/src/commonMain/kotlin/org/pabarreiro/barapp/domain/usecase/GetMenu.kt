package org.pabarreiro.barapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.pabarreiro.barapp.domain.model.Categoria
import org.pabarreiro.barapp.domain.model.Producto
import org.pabarreiro.barapp.domain.repository.BarRepository

class GetMenu(private val repository: BarRepository) {
    fun getCategorias(): Flow<List<Categoria>> = repository.getCategorias()
    fun getProductos(categoriaId: Long?): Flow<List<Producto>> = repository.getProductos(categoriaId)
    suspend fun syncMenu() = repository.syncMenu()
}
