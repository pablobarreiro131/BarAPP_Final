package org.pabarreiro.barapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.pabarreiro.barapp.domain.model.Mesa
import org.pabarreiro.barapp.presentation.ui.theme.*
import org.pabarreiro.barapp.presentation.viewmodel.TablesUiState
import org.pabarreiro.barapp.presentation.viewmodel.TablesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
    onTableFree: (Long) -> Unit,
    onTableOccupied: (Long) -> Unit,
    viewModel: TablesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "MESAS",
                        style = LuxuryTypography.titleLarge.copy(fontWeight = FontWeight.Medium),
                        color = PrimaryIvory
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshTables() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar", tint = PrimaryIvory)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgDark,
                    titleContentColor = PrimaryIvory
                )
            )
        },
        containerColor = BgDark
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is TablesUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryIvory
                )
                is TablesUiState.Error -> Text(
                    text = state.message,
                    color = AccentRed,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    style = LuxuryTypography.bodyLarge
                )
                is TablesUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(140.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.mesas) { mesa ->
                            TableCard(
                                mesa = mesa,
                                onClick = {
                                    when (mesa.estado.lowercase()) {
                                        "libre" -> onTableFree(mesa.id)
                                        "ocupada" -> onTableOccupied(mesa.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCard(mesa: Mesa, onClick: () -> Unit) {
    val estado = mesa.estado.lowercase()
    val isOcupada = estado == "ocupada"
    val isReservada = estado == "reservada"
    val isBlocked = isReservada

    val backgroundColor = when (estado) {
        "ocupada" -> BgSurfaceElevated
        "reservada" -> BgSurface
        else -> BgDark
    }
    val borderColor = when (estado) {
        "ocupada" -> PrimaryIvoryMuted.copy(alpha = 0.3f)
        "reservada" -> BorderSubtle
        else -> BorderStrong
    }
    val textColor = when (estado) {
        "ocupada" -> PrimaryIvory
        "reservada" -> TextMuted.copy(alpha = 0.5f)
        else -> TextMuted
    }
    val statusColor = when (estado) {
        "ocupada" -> AccentAmber
        "reservada" -> TextMuted.copy(alpha = 0.4f)
        else -> TextMuted
    }
    val indicatorColor: Color? = when (estado) {
        "ocupada" -> AccentAmber
        "reservada" -> BorderStrong
        else -> null
    }

    Card(
        modifier = Modifier
            .size(140.dp)
            .then(if (!isBlocked) Modifier.clickable { onClick() } else Modifier)
            .border(1.dp, borderColor, RoundedCornerShape(0.dp)),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (indicatorColor != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(indicatorColor)
                        .align(Alignment.TopCenter)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "MESA",
                    style = LuxuryTypography.labelSmall,
                    color = textColor.copy(alpha = 0.6f)
                )
                Text(
                    text = "${mesa.numeroMesa}",
                    style = LuxuryTypography.headlineLarge.copy(fontWeight = FontWeight.Light),
                    color = textColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${mesa.capacidad} PAX",
                    style = LuxuryTypography.labelSmall,
                    color = TextMuted.copy(alpha = if (isBlocked) 0.3f else 1f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = estado.uppercase(),
                    style = LuxuryTypography.labelSmall,
                    color = statusColor
                )
            }
            if (isBlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgDark.copy(alpha = 0.4f))
                )
            }
        }
    }
}
