package org.pabarreiro.barapp.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.pabarreiro.barapp.domain.model.Categoria
import org.pabarreiro.barapp.domain.model.Producto
import org.pabarreiro.barapp.presentation.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    mesaId: Long,
    onBack: () -> Unit,
    viewModel: MenuViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mesa $mesaId - Carta") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            CategorySelector(
                categorias = uiState.categorias,
                selectedId = uiState.selectedCategoriaId,
                onSelected = { viewModel.selectCategoria(it) }
            )
            
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            ProductList(
                productos = uiState.productos,
                onProductClick = { /* TODO: Añadir orden */ }
            )
        }
    }
}

@Composable
fun CategorySelector(
    categorias: List<Categoria>,
    selectedId: Long?,
    onSelected: (Long?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedId == null,
                onClick = { onSelected(null) },
                label = { Text("Todo") }
            )
        }
        items(categorias) { cat ->
            FilterChip(
                selected = selectedId == cat.id,
                onClick = { onSelected(cat.id) },
                label = { Text(cat.nombre) }
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
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(productos) { producto ->
            ListItem(
                headlineContent = { Text(producto.nombre) },
                supportingContent = { Text("${producto.precio}€") },
                trailingContent = { 
                    Button(onClick = { onProductClick(producto) }) {
                        Text("Añadir")
                    }
                },
                modifier = Modifier.clickable { onProductClick(producto) }
            )
            HorizontalDivider()
        }
    }
}
