package org.pabarreiro.barapp.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.pabarreiro.barapp.domain.model.Comanda
import org.pabarreiro.barapp.domain.model.DetalleComanda
import org.pabarreiro.barapp.domain.model.Perfil
import org.pabarreiro.barapp.domain.repository.BarRepository

class GetActiveComandaUseCase(private val repository: BarRepository) {
    operator fun invoke(mesaId: Long): Flow<Comanda?> {
        return repository.getComandasActivas(mesaId).map { it.firstOrNull() }
    }
}

class CreateComandaUseCase(private val repository: BarRepository) {
    suspend operator fun invoke(mesaId: Long, camarero: Perfil): Result<Comanda> {
        val tempId = "temp-${Clock.System.now().toEpochMilliseconds()}"
        val now = Clock.System.now()
        val comanda = Comanda(
            id = tempId,
            mesaId = mesaId,
            camareroId = camarero.id,
            fechaApertura = now,
            fechaCierre = null,
            estadoPago = false,
            total = 0.0,
            detalles = emptyList()
        )
        return repository.createComanda(comanda)
    }
}

class AddDetalleUseCase(private val repository: BarRepository) {
    suspend operator fun invoke(comandaId: String, productoId: Long, cantidad: Int = 1): Result<DetalleComanda> {
        val detalle = DetalleComanda(
            id = null,
            comandaId = comandaId,
            productoId = productoId,
            cantidad = cantidad,
            precioUnitario = 0.0
        )
        return repository.addDetalleAComanda(comandaId, detalle)
    }
}

class PagarComandaUseCase(private val repository: BarRepository) {
    suspend operator fun invoke(comandaId: String): Result<Unit> {
        return repository.pagarComanda(comandaId)
    }
}

class RemoveDetalleUseCase(private val repository: BarRepository) {
    suspend operator fun invoke(comandaId: String, detalleId: Long, mesaId: Long): Result<Unit> {
        return repository.deleteDetalleFromComanda(comandaId, detalleId, mesaId)
    }
}
