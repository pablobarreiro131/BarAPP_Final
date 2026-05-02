package org.pabarreiro.barapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.pabarreiro.barapp.domain.model.Categoria
import org.pabarreiro.barapp.domain.model.Comanda
import org.pabarreiro.barapp.domain.model.DetalleComanda
import org.pabarreiro.barapp.domain.model.Producto
import org.pabarreiro.barapp.presentation.ui.theme.*
import org.pabarreiro.barapp.presentation.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(mesaId: Long, onBack: () -> Unit, viewModel: MenuViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mesaId) { viewModel.setMesaId(mesaId) }

    var showComandaSheet by remember { mutableStateOf(false) }
    var showPayDialog by remember { mutableStateOf(false) }
    var showTicket by remember { mutableStateOf(false) }
    var lastComanda by remember { mutableStateOf<Comanda?>(null) }

    val mesaLabel = uiState.mesa?.let { "MESA ${it.numeroMesa}" } ?: "MESA $mesaId"

    LaunchedEffect(uiState.pagada) {
        if (uiState.pagada) {
            if (lastComanda == null) {
                lastComanda = uiState.activeComanda
            }
            showTicket = true
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(
                                    mesaLabel,
                                    style =
                                            LuxuryTypography.titleLarge.copy(
                                                    fontWeight = FontWeight.Medium
                                            ),
                                    color = PrimaryIvory
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = PrimaryIvory
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
                )
            },
            containerColor = BgDark,
            floatingActionButton = {
                val totalItems = uiState.activeComanda?.detalles?.sumOf { it.cantidad } ?: 0
                if (totalItems > 0) {
                    FloatingActionButton(
                            onClick = { showComandaSheet = true },
                            containerColor = PrimaryIvory,
                            contentColor = BgDark,
                            shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                                "COMANDA ($totalItems)",
                                style = LuxuryTypography.labelLarge,
                                modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            CategorySelector(
                    categorias = uiState.categorias,
                    selectedId = uiState.selectedCategoriaId,
                    onSelected = { viewModel.selectCategoria(it) }
            )

            if (uiState.isLoading || uiState.isComandaActionLoading) {
                LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(1.dp),
                        color = PrimaryIvory,
                        trackColor = BgDark
                )
            } else {
                Spacer(modifier = Modifier.height(1.dp))
            }

            uiState.error?.let {
                Text(
                        text = it,
                        color = AccentRed,
                        style = LuxuryTypography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                )
            }

            ProductList(
                    productos = uiState.productos,
                    onProductClick = { viewModel.addProductToOrder(it) }
            )
        }

        if (showComandaSheet) {
            ModalBottomSheet(
                    onDismissRequest = { showComandaSheet = false },
                    containerColor = BgSurfaceElevated,
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
                    dragHandle = { BottomSheetDefaults.DragHandle(color = BorderStrong) }
            ) {
                ComandaSheetContent(
                        comanda = uiState.activeComanda,
                        isActionLoading = uiState.isComandaActionLoading,
                        onRemoveDetalle = { detalleId ->
                            viewModel.removeDetalleFromOrder(detalleId)
                        },
                        onPagar = { showPayDialog = true }
                )
            }
        }
    }

    if (showPayDialog) {
        val total =
                uiState.activeComanda?.detalles?.sumOf { it.precioUnitario * it.cantidad } ?: 0.0
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
                            Text("TOTAL", style = LuxuryTypography.labelLarge, color = TextMuted)
                            Text(
                                    "%.2f€".format(total),
                                    style =
                                            LuxuryTypography.titleLarge.copy(
                                                    fontWeight = FontWeight.Medium
                                            ),
                                    color = PrimaryIvory
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                            onClick = {
                                showPayDialog = false
                                showComandaSheet = false
                                lastComanda = uiState.activeComanda
                                viewModel.pagarComanda()
                            },
                            colors =
                                    ButtonDefaults.buttonColors(
                                            containerColor = PrimaryIvory,
                                            contentColor = BgDark
                                    ),
                            shape = RoundedCornerShape(0.dp)
                    ) { Text("PAGAR Y CERRAR", style = LuxuryTypography.labelLarge) }
                },
                dismissButton = {
                    TextButton(
                            onClick = { showPayDialog = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                    ) { Text("CANCELAR", style = LuxuryTypography.labelSmall) }
                }
        )
    }

    if (showTicket && lastComanda != null) {
        androidx.compose.ui.window.Dialog(
                onDismissRequest = {},
                properties =
                        androidx.compose.ui.window.DialogProperties(
                                usePlatformDefaultWidth = false,
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false
                        )
        ) {
            TicketView(
                    comanda = lastComanda!!,
                    mesa = uiState.mesa,
                    onClose = {
                        showTicket = false
                        onBack()
                    }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(categorias: List<Categoria>, selectedId: Long?, onSelected: (Long?) -> Unit) {
    LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().background(BgDark)
    ) {
        item {
            FilterChip(
                    selected = selectedId == null,
                    onClick = { onSelected(null) },
                    label = {
                        Text(
                                "TODO",
                                style = LuxuryTypography.labelSmall,
                                color = if (selectedId == null) BgDark else TextMuted
                        )
                    },
                    colors =
                            FilterChipDefaults.filterChipColors(
                                    containerColor = BgDark,
                                    selectedContainerColor = PrimaryIvory,
                            ),
                    border =
                            FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedId == null,
                                    borderColor = BorderStrong,
                                    selectedBorderColor = PrimaryIvory
                            ),
                    shape = RoundedCornerShape(0.dp)
            )
        }
        items(categorias) { cat ->
            FilterChip(
                    selected = selectedId == cat.id,
                    onClick = { onSelected(cat.id) },
                    label = {
                        Text(
                                cat.nombre.uppercase(),
                                style = LuxuryTypography.labelSmall,
                                color = if (selectedId == cat.id) BgDark else TextMuted
                        )
                    },
                    colors =
                            FilterChipDefaults.filterChipColors(
                                    containerColor = BgDark,
                                    selectedContainerColor = PrimaryIvory,
                            ),
                    border =
                            FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedId == cat.id,
                                    borderColor = BorderStrong,
                                    selectedBorderColor = PrimaryIvory
                            ),
                    shape = RoundedCornerShape(0.dp)
            )
        }
    }
}

@Composable
fun ProductList(productos: List<Producto>, onProductClick: (Producto) -> Unit) {
    LazyColumn(
            modifier = Modifier.fillMaxSize().background(BgDark),
            contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(productos) { producto ->
            Row(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .clickable { onProductClick(producto) }
                                    .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            producto.nombre.uppercase(),
                            style = LuxuryTypography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = TextPrimary
                    )
                }
                Text(
                        "%.2f€".format(producto.precio),
                        style = LuxuryTypography.bodyLarge,
                        color = PrimaryIvory
                )
            }
            HorizontalDivider(color = BorderSubtle, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun ComandaSheetContent(
        comanda: Comanda?,
        isActionLoading: Boolean,
        onRemoveDetalle: (Long) -> Unit,
        onPagar: () -> Unit
) {
    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 8.dp, bottom = 32.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text("COMANDA ACTUAL", style = LuxuryTypography.titleLarge, color = PrimaryIvory)
            if (isActionLoading) {
                CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = PrimaryIvory,
                        strokeWidth = 2.dp
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        if (comanda == null || comanda.detalles.isEmpty()) {
            Text(
                    "No hay productos en la comanda.",
                    style = LuxuryTypography.bodyLarge,
                    color = TextMuted
            )
        } else {
            LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(comanda.detalles, key = { it.id ?: 0 }) { detalle ->
                    SheetDetalleRow(
                            detalle = detalle,
                            onRemove = { onRemoveDetalle(detalle.id ?: 0) }
                    )
                    HorizontalDivider(color = BorderSubtle)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderStrong)
            Spacer(modifier = Modifier.height(12.dp))

            // Total calculado localmente usando precioUnitario * cantidad
            val total = comanda.detalles.sumOf { it.precioUnitario * it.cantidad }
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text("TOTAL", style = LuxuryTypography.labelLarge, color = TextMuted)
                Text(
                        "%.2f€".format(total),
                        style = LuxuryTypography.headlineLarge.copy(fontWeight = FontWeight.Light),
                        color = PrimaryIvory
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                    onClick = onPagar,
                    enabled = !isActionLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(0.dp),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = PrimaryIvory,
                                    contentColor = BgDark
                            )
            ) { Text("PAGAR Y CERRAR", style = LuxuryTypography.labelLarge) }
        }
    }
}

@Composable
private fun SheetDetalleRow(detalle: DetalleComanda, onRemove: () -> Unit) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
                "${detalle.cantidad}×",
                style = LuxuryTypography.labelLarge,
                color = TextMuted,
                modifier = Modifier.width(28.dp)
        )
        Text(
                (detalle.nombreProducto ?: "Producto #${detalle.productoId}").uppercase(),
                style = LuxuryTypography.bodyLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
        )
        Text(
                "%.2f€".format(detalle.precioUnitario * detalle.cantidad),
                style = LuxuryTypography.bodyLarge,
                color = PrimaryIvory,
                modifier = Modifier.padding(horizontal = 8.dp)
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = AccentRed.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
            )
        }
    }
}
