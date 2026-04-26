package org.pabarreiro.barapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.pabarreiro.barapp.domain.model.Mesa
import org.pabarreiro.barapp.presentation.ui.theme.*
import org.pabarreiro.barapp.presentation.viewmodel.TablesUiState
import org.pabarreiro.barapp.presentation.viewmodel.TablesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(
    onTableSelected: (Long) -> Unit,
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
                            TableCard(mesa = mesa, onClick = { onTableSelected(mesa.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCard(mesa: Mesa, onClick: () -> Unit) {
    val isOcupada = mesa.estado.lowercase() == "ocupada"
    
    val backgroundColor = if (isOcupada) BgSurfaceElevated else BgDark
    val borderColor = if (isOcupada) BorderSubtle else BorderStrong
    val textColor = if (isOcupada) PrimaryIvory else TextMuted
    val statusColor = if (isOcupada) AccentAmber else TextMuted

    Card(
        modifier = Modifier
            .size(140.dp)
            .clickable { onClick() }
            .border(1.dp, borderColor, RoundedCornerShape(0.dp)),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MESA ${mesa.numeroMesa}", 
                style = LuxuryTypography.titleLarge.copy(fontWeight = FontWeight.Light),
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${mesa.capacidad} PAX", 
                style = LuxuryTypography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = mesa.estado.uppercase(), 
                style = LuxuryTypography.labelSmall,
                color = statusColor
            )
        }
    }
}
