package org.pabarreiro.barapp.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.pabarreiro.barapp.domain.model.Mesa
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
            TopAppBar(title = { Text("Mesas") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is TablesUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is TablesUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is TablesUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(120.dp),
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
    val backgroundColor = if (mesa.estado == "libre") Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
    
    Card(
        modifier = Modifier.size(120.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Mesa ${mesa.numeroMesa}", style = MaterialTheme.typography.titleLarge)
            Text("${mesa.capacidad} pax", style = MaterialTheme.typography.bodyMedium)
            Text(mesa.estado.uppercase(), style = MaterialTheme.typography.labelSmall)
        }
    }
}
