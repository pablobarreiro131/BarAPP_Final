package org.pabarreiro.barapp.data.mapper

import org.pabarreiro.barapp.data.local.entity.DetalleComandaEntity
import org.pabarreiro.barapp.data.remote.dto.DetalleComandaDTO
import org.pabarreiro.barapp.data.remote.dto.ProductoDTO
import org.pabarreiro.barapp.domain.model.DetalleComanda
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MappersTest {

    @Test
    fun remoteDetalleToDomainMapping() {
        val dto = DetalleComandaDTO(
            id = 1L,
            comandaId = "comanda-1",
            productoId = 10L,
            producto = ProductoDTO(id = 10L, nombre = "Cerveza", precio = 2.5),
            cantidad = 2,
            precioUnitario = 2.5
        )

        val domain = dto.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("comanda-1", domain.comandaId)
        assertEquals(10L, domain.productoId)
        assertEquals(2, domain.cantidad)
        assertEquals(2.5, domain.precioUnitario)
        assertEquals("Cerveza", domain.nombreProducto)
    }

    @Test
    fun localDetalleToDomainMapping() {
        val entity = DetalleComandaEntity(
            id = 1L,
            comandaId = "comanda-1",
            productoId = 10L,
            cantidad = 3,
            precioUnitario = 1.5,
            nombreProducto = "Café",
            isSynced = true
        )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("Café", domain.nombreProducto)
        assertEquals(3, domain.cantidad)
    }

    @Test
    fun domainDetalleToEntityMapping() {
        val domain = DetalleComanda(
            id = 5L,
            comandaId = "uuid-test",
            productoId = 20L,
            cantidad = 1,
            precioUnitario = 10.0,
            nombreProducto = "Vino"
        )

        val entity = domain.toEntity(isSynced = true)

        assertEquals(5L, entity.id)
        assertEquals("Vino", entity.nombreProducto)
        assertEquals("uuid-test", entity.comandaId)
    }
}
