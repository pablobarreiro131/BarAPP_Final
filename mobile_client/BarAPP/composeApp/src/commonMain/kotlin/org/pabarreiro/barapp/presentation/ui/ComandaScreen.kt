package org.pabarreiro.barapp.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import org.pabarreiro.barapp.domain.model.DetalleComanda
import org.pabarreiro.barapp.presentation.ui.theme.*
import org.pabarreiro.barapp.presentation.viewmodel.ComandaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComandaScreen(
    mesaId: Long,
    onBack: () -> Unit,
    onAddProducts: () -> Unit,
    viewModel: ComandaViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPayDialog by remember { mutableStateOf(false) }

    LaunchedEffect(mesaId) {
        viewModel.setMesaId(mesaId)
    }

    LaunchedEffect(uiState.pagada) {
        if (uiState.pagada) onBack()
    }

    val mesaLabel = uiState.mesa?.let { "MESA ${it.numeroMesa}" } ?: "COMANDA"
    val comanda = uiState.comanda
    val totalLocal = comanda?.detalles?.sumOf { it.precioUnitario * it.cantidad } ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            mesaLabel,
                            style = LuxuryTypography.titleLarge.copy(fontWeight = FontWeight.Medium),
                            color = PrimaryIvory
                        )
                        if (comanda != null) {
                            Text(
                                "Comanda activa",
                                style = LuxuryTypography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = PrimaryIvory
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onAddProducts,
                        colors = ButtonDefaults.textButtonColors(contentColor = PrimaryIvory)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AÑADIR", style = LuxuryTypography.labelSmall)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        },
        containerColor = BgDark,
        bottomBar = {
            if (comanda != null && comanda.detalles.isNotEmpty()) {
                PayBottomBar(
                    total = totalLocal,
                    isLoading = uiState.isActionLoading,
                    onPagar = { showPayDialog = true }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryIvory
                )

                comanda == null || comanda.detalles.isEmpty() -> EmptyComandaView(onAddProducts)

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {

                        item {
                            ComandaInfoHeader(
                                comanda = comanda,
                                isActionLoading = uiState.isActionLoading
                            )
                        }

                        items(comanda.detalles, key = { it.id ?: 0 }) { detalle ->
                            DetalleItem(
                                detalle = detalle,
                                onRemove = {
                                    detalle.id?.let { viewModel.removeDetalle(it) }
                                }
                            )
                            HorizontalDivider(
                                color = BorderSubtle,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }


                        item {
                            TotalRow(total = totalLocal)
                        }
                    }
                }
            }

            uiState.error?.let { errorMsg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK", color = PrimaryIvory)
                        }
                    },
                    containerColor = AccentRed.copy(alpha = 0.9f)
                ) {
                    Text(errorMsg, color = PrimaryIvory, style = LuxuryTypography.bodyLarge)
                }
            }
        }
    }

    // Diálogo de confirmación de pago
    if (showPayDialog) {
        AlertDialog(
            onDismissRequest = { showPayDialog = false },
            containerColor = BgSurfaceElevated,
            shape = RoundedCornerShape(0.dp),
            title = {
                Text(
                    "CONFIRMAR PAGO",
                    style = LuxuryTypography.titleLarge,
                    color = PrimaryIvory
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Se cerrará la comanda y la mesa quedará libre.",
                        style = LuxuryTypography.bodyLarge,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "TOTAL",
                            style = LuxuryTypography.labelLarge,
                            color = TextMuted
                        )
                        Text(
                            "%.2f€".format(totalLocal),
                            style = LuxuryTypography.titleLarge.copy(fontWeight = FontWeight.Medium),
                            color = PrimaryIvory
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPayDialog = false
                        viewModel.pagarComanda()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryIvory,
                        contentColor = BgDark
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("PAGAR Y CERRAR", style = LuxuryTypography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPayDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                ) {
                    Text("CANCELAR", style = LuxuryTypography.labelSmall)
                }
            }
        )
    }
}

@Composable
private fun ComandaInfoHeader(
    comanda: org.pabarreiro.barapp.domain.model.Comanda,
    isActionLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSurfaceElevated)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${comanda.detalles.size} ARTÍCULO${if (comanda.detalles.size != 1) "S" else ""}",
                style = LuxuryTypography.labelSmall,
                color = TextMuted
            )
            if (isActionLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = PrimaryIvory,
                    strokeWidth = 1.5.dp
                )
            }
        }
    }
    if (!isActionLoading) {
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth().height(1.dp),
            color = BorderSubtle,
            trackColor = BorderSubtle
        )
    } else {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(1.dp),
            color = PrimaryIvory,
            trackColor = BgDark
        )
    }
}

@Composable
private fun DetalleItem(
    detalle: DetalleComanda,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cantidad badge
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BgSurfaceElevated, RoundedCornerShape(0.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${detalle.cantidad}",
                style = LuxuryTypography.labelLarge.copy(fontSize = 12.sp),
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                (detalle.nombreProducto ?: "Producto #${detalle.productoId}").uppercase(),
                style = LuxuryTypography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary
            )
            if (detalle.precioUnitario > 0) {
                Text(
                    "%.2f€/u".format(detalle.precioUnitario),
                    style = LuxuryTypography.labelSmall,
                    color = TextMuted
                )
            }
        }

        Text(
            "%.2f€".format(detalle.precioUnitario * detalle.cantidad),
            style = LuxuryTypography.bodyLarge,
            color = PrimaryIvory,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = AccentRed.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun TotalRow(total: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        HorizontalDivider(color = BorderStrong)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TOTAL",
                style = LuxuryTypography.labelLarge,
                color = TextMuted
            )
            Text(
                "%.2f€".format(total),
                style = LuxuryTypography.headlineLarge.copy(fontWeight = FontWeight.Light),
                color = PrimaryIvory
            )
        }
    }
}

@Composable
private fun PayBottomBar(
    total: Double,
    isLoading: Boolean,
    onPagar: () -> Unit
) {
    Surface(
        color = BgSurfaceElevated,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = onPagar,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryIvory,
                    contentColor = BgDark,
                    disabledContainerColor = PrimaryIvoryMuted
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = BgDark,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "PAGAR  ·  %.2f€".format(total),
                        style = LuxuryTypography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyComandaView(onAddProducts: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "—",
            style = LuxuryTypography.displayLarge,
            color = BorderStrong
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "COMANDA VACÍA",
            style = LuxuryTypography.labelLarge,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Añade productos para comenzar",
            style = LuxuryTypography.bodyLarge,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedButton(
            onClick = onAddProducts,
            shape = RoundedCornerShape(0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderStrong),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryIvory)
        ) {
            Text("VER CARTA", style = LuxuryTypography.labelLarge)
        }
    }
}
