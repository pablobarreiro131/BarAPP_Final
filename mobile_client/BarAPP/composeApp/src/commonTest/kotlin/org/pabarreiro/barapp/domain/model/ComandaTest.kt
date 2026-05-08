package org.pabarreiro.barapp.domain.model

import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class ComandaTest {

    @Test
    fun calculateTotalCorrectly() {
        val detalles = listOf(
            DetalleComanda(
                comandaId = "1",
                productoId = 1,
                cantidad = 2,
                precioUnitario = 2.5,
                nombreProducto = "Cerveza"
            ),
            DetalleComanda(
                comandaId = "1",
                productoId = 2,
                cantidad = 1,
                precioUnitario = 5.0,
                nombreProducto = "Hamburguesa"
            )
        )

        val comanda = Comanda(
            id = "1",
            mesaId = 1,
            camareroId = "camarero-uuid",
            fechaApertura = Clock.System.now(),
            detalles = detalles,
            total = 0.0,
            fechaCierre = null,
            estadoPago = false
        )

        val calculatedTotal = comanda.detalles.sumOf { it.precioUnitario * it.cantidad }
        
        assertEquals(10.0, calculatedTotal)
    }
}
