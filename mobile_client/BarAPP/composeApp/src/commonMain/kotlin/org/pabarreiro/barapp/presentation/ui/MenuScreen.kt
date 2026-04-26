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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.pabarreiro.barapp.domain.model.Categoria
import org.pabarreiro.barapp.domain.model.Comanda
import org.pabarreiro.barapp.domain.model.Producto
import org.pabarreiro.barapp.presentation.ui.theme.*
import org.pabarreiro.barapp.presentation.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    mesaId: Long,
    onBack: () -> Unit,
    viewModel: MenuViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(mesaId) {
        viewModel.setMesaId(mesaId)
    }

    var showComandaSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "MESA $mesaId", 
                        style = LuxuryTypography.titleLarge.copy(fontWeight = FontWeight.Medium),
                        color = PrimaryIvory
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryIvory)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgDark
                )
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
                    productos = uiState.productos,
                    onPagar = {
                        viewModel.pagarComanda()
                        showComandaSheet = false
                        onBack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    categorias: List<Categoria>,
    selectedId: Long?,
    onSelected: (Long?) -> Unit
) {
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
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BgDark,
                    selectedContainerColor = PrimaryIvory,
                ),
                border = FilterChipDefaults.filterChipBorder(
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
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = BgDark,
                    selectedContainerColor = PrimaryIvory,
                ),
                border = FilterChipDefaults.filterChipBorder(
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
fun ProductList(
    productos: List<Producto>,
    onProductClick: (Producto) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BgDark),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(productos) { producto ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    "${producto.precio}€",
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
    productos: List<Producto>,
    onPagar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            "COMANDA ACTUAL",
            style = LuxuryTypography.titleLarge,
            color = PrimaryIvory
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (comanda == null || comanda.detalles.isEmpty()) {
            Text(
                "No hay productos en la comanda.",
                style = LuxuryTypography.bodyLarge,
                color = TextMuted
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(comanda.detalles) { detalle ->

                    val productName = productos.find { it.id == detalle.productoId }?.nombre ?: "Producto #${detalle.productoId}"
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${detalle.cantidad}x $productName",
                            style = LuxuryTypography.bodyLarge,
                            color = TextPrimary
                        )
                        Text(
                            "${detalle.cantidad * (productos.find { it.id == detalle.productoId }?.precio ?: 0.0)}€",
                            style = LuxuryTypography.bodyLarge,
                            color = TextMuted
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = BorderStrong)
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onPagar,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryIvory,
                    contentColor = BgDark
                )
            ) {
                Text(
                    "PAGAR Y CERRAR",
                    style = LuxuryTypography.labelLarge
                )
            }
        }
    }
}
