package org.pabarreiro.barapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.pabarreiro.barapp.domain.model.Comanda
import org.pabarreiro.barapp.domain.model.Mesa
import org.pabarreiro.barapp.presentation.ui.theme.*

@Composable
fun TicketView(
    comanda: Comanda,
    mesa: Mesa?,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark.copy(alpha = 0.85f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFDFDFD))
                .padding(20.dp)
        ) {

            Text(
                "BAR APP PREMIUM",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Text(
                "Calle de la Gastronomía, 123",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            DashedDivider()
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "MESA: ${mesa?.numeroMesa ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Text(
                    "ID: ${comanda.id.takeLast(6).uppercase()}",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = Color.Black
                )
            }

            Text(
                "FECHA: ${formatDate(comanda.fechaApertura)}",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            DashedDivider()
            
            Spacer(modifier = Modifier.height(12.dp))

            // Items
            Column(modifier = Modifier.weight(1f, fill = false)) {
                comanda.detalles.forEach { detalle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                (detalle.nombreProducto ?: "Producto").uppercase(),
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                color = Color.Black
                            )
                            Text(
                                "${detalle.cantidad} x %.2f€".format(detalle.precioUnitario),
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = Color.DarkGray
                            )
                        }
                        Text(
                            "%.2f€".format(detalle.precioUnitario * detalle.cantidad),
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                            color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            DashedDivider()
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TOTAL",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold),
                    color = Color.Black
                )
                Text(
                    "%.2f€".format(comanda.detalles.sumOf { it.precioUnitario * it.cantidad }),
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "¡GRACIAS POR SU VISITA!",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Text(
                "*** IVA INCLUIDO ***",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BgDark, contentColor = Color.White),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text("CERRAR Y VOLVER", style = LuxuryTypography.labelLarge)
            }
        }
    }
}

@Composable
fun DashedDivider() {
    Text(
        "- - - - - - - - - - - - - - - - - - - - - -",
        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
        color = Color.LightGray,
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1
    )
}

fun formatDate(instant: kotlinx.datetime.Instant): String {
    val dt = instant.toString()
    return dt.substring(0, 10) + " " + dt.substring(11, 16)
}
