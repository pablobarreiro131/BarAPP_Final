package org.pabarreiro.barapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.pabarreiro.barapp.domain.model.Categoria
import org.pabarreiro.barapp.domain.model.Comanda
import org.pabarreiro.barapp.domain.model.Mesa
import org.pabarreiro.barapp.domain.model.Producto
import org.pabarreiro.barapp.domain.repository.BarRepository
import org.pabarreiro.barapp.domain.usecase.*

data class MenuUiState(
        val mesa: Mesa? = null,
        val categorias: List<Categoria> = emptyList(),
        val productos: List<Producto> = emptyList(),
        val selectedCategoriaId: Long? = null,
        val activeComanda: Comanda? = null,
        val isLoading: Boolean = false,
        val isComandaActionLoading: Boolean = false,
        val error: String? = null
)

class MenuViewModel(
        private val getMenu: GetMenu,
        private val getActiveComandaUseCase: GetActiveComandaUseCase,
        private val createComandaUseCase: CreateComandaUseCase,
        private val addDetalleUseCase: AddDetalleUseCase,
        private val pagarComandaUseCase: PagarComandaUseCase,
        private val removeDetalleUseCase: RemoveDetalleUseCase,
        private val repository: BarRepository
) : ViewModel() {

    private val _selectedCategoriaId = MutableStateFlow<Long?>(null)
    private val _mesaId = MutableStateFlow<Long?>(null)
    private val _mesa = MutableStateFlow<Mesa?>(null)
    private val _isComandaActionLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MenuUiState> =
            combine(
                            combine(
                                    getMenu.getCategorias(),
                                    _selectedCategoriaId.flatMapLatest { getMenu.getProductos(it) },
                                    _selectedCategoriaId,
                                    ::Triple
                            ),
                            combine(
                                    _mesaId.flatMapLatest { id ->
                                        if (id != null) getActiveComandaUseCase(id)
                                        else flowOf(null)
                                    },
                                    _isComandaActionLoading,
                                    _error,
                                    ::Triple
                            ),
                            _mesa
                    ) {
                            (categorias, productos, selectedId),
                            (activeComanda, isComandaActionLoading, error),
                            mesa ->
                        MenuUiState(
                                mesa = mesa,
                                categorias = categorias,
                                productos = productos,
                                selectedCategoriaId = selectedId,
                                activeComanda = activeComanda,
                                isLoading = false,
                                isComandaActionLoading = isComandaActionLoading,
                                error = error
                        )
                    }
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = MenuUiState(isLoading = true)
                    )

    init {
        viewModelScope.launch {
            val result = getMenu.syncMenu()
            if (!result.isSuccess) {
                _error.value = "Error sincronizando menú: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun setMesaId(mesaId: Long) {
        if (_mesaId.value != mesaId) {
            _mesaId.value = mesaId
            viewModelScope.launch {
                _mesa.value = repository.getMesa(mesaId)
                val result = repository.syncComandasMesa(mesaId)
                if (!result.isSuccess) {
                    _error.value =
                            "Error al sincronizar las comandas de la mesa: ${result.exceptionOrNull()?.message}"
                }
            }
        }
    }

    fun selectCategoria(id: Long?) {
        _selectedCategoriaId.value = id
    }

    fun addProductToOrder(producto: Producto) {
        val currentMesaId = _mesaId.value ?: return

        viewModelScope.launch {
            _isComandaActionLoading.value = true
            _error.value = null

            try {
                var comandaId = uiState.value.activeComanda?.id

                if (comandaId == null) {
                    val perfil = repository.getCurrentUser().firstOrNull()
                    if (perfil == null) {
                        _error.value = "Error: Usuario no autenticado"
                        return@launch
                    }

                    val createResult = createComandaUseCase(currentMesaId, perfil)
                    if (createResult.isSuccess) {
                        comandaId = createResult.getOrThrow().id
                    } else {
                        _error.value =
                                "Error creando comanda: ${createResult.exceptionOrNull()?.message}"
                        return@launch
                    }
                }

                if (comandaId != null) {
                    val addResult = addDetalleUseCase(comandaId, producto.id, 1)
                    if (!addResult.isSuccess) {
                        _error.value =
                                "Error añadiendo producto: ${addResult.exceptionOrNull()?.message}"
                    } else {
                        repository.syncComandasMesa(currentMesaId)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
            } finally {
                _isComandaActionLoading.value = false
            }
        }
    }

    fun removeDetalleFromOrder(detalleId: Long) {
        val currentMesaId = _mesaId.value ?: return
        val comandaId = uiState.value.activeComanda?.id ?: return

        viewModelScope.launch {
            _isComandaActionLoading.value = true
            _error.value = null
            val result = removeDetalleUseCase(comandaId, detalleId, currentMesaId)
            if (!result.isSuccess) {
                _error.value = "Error eliminando producto: ${result.exceptionOrNull()?.message}"
            }
            _isComandaActionLoading.value = false
        }
    }

    fun pagarComanda() {
        val comandaId = uiState.value.activeComanda?.id ?: return

        viewModelScope.launch {
            _isComandaActionLoading.value = true
            _error.value = null

            val result = pagarComandaUseCase(comandaId)
            if (!result.isSuccess) {
                _error.value = "Error al pagar comanda: ${result.exceptionOrNull()?.message}"
            }

            _isComandaActionLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
