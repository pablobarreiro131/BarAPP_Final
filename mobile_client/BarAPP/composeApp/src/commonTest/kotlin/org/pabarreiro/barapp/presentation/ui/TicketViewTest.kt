package org.pabarreiro.barapp.presentation.ui

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class TicketViewTest {

    @Test
    fun formatDateCorrectly() {

        val instant = Instant.parse("2024-05-08T18:30:00Z")
        val formatted = formatDate(instant)

        assertEquals("2024-05-08 18:30", formatted)
    }
}
