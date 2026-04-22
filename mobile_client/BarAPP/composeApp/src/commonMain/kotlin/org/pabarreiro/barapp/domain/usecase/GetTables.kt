package org.pabarreiro.barapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.pabarreiro.barapp.domain.model.Mesa
import org.pabarreiro.barapp.domain.repository.BarRepository

class GetTables(private val repository: BarRepository) {
    operator fun invoke(): Flow<List<Mesa>> = repository.getMesas()
}
