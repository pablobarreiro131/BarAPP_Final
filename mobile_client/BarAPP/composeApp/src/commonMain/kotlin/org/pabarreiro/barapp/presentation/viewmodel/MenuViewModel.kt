package org.pabarreiro.barapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.pabarreiro.barapp.domain.model.Categoria
import org.pabarreiro.barapp.domain.model.Producto
import org.pabarreiro.barapp.domain.usecase.GetMenu

data class MenuUiState(
    val categorias: List<Categoria> = emptyList(),
    val productos: List<Producto> = emptyList(),
    val selectedCategoriaId: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MenuViewModel(
    private val getMenu: GetMenu
) : ViewModel() {

    private val _selectedCategoriaId = MutableStateFlow<Long?>(null)
    
    val uiState: StateFlow<MenuUiState> = combine(
        getMenu.getCategorias(),
        _selectedCategoriaId.flatMapLatest { getMenu.getProductos(it) },
        _selectedCategoriaId
    ) { categorias, productos, selectedId ->
        MenuUiState(
            categorias = categorias,
            productos = productos,
            selectedCategoriaId = selectedId,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MenuUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            println("[BarApp] [MenuViewModel] Sincronizando menú con el servidor...")
            val result = getMenu.syncMenu()
            if (result.isSuccess) {
                println("[BarApp] [MenuViewModel] Menú sincronizado exitosamente")
            } else {
                println("[BarApp] [MenuViewModel] Error sincronizando menú: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun selectCategoria(id: Long?) {
        println("[BarApp] [MenuViewModel] Categoría seleccionada: $id")
        _selectedCategoriaId.value = id
    }
}
