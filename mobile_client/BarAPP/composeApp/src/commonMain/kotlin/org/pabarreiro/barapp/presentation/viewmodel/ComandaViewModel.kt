package org.pabarreiro.barapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.pabarreiro.barapp.domain.model.Comanda
import org.pabarreiro.barapp.domain.model.Mesa
import org.pabarreiro.barapp.domain.repository.BarRepository
import org.pabarreiro.barapp.domain.usecase.GetActiveComandaUseCase
import org.pabarreiro.barapp.domain.usecase.PagarComandaUseCase
import org.pabarreiro.barapp.domain.usecase.RemoveDetalleUseCase

data class ComandaUiState(
    val mesa: Mesa? = null,
    val comanda: Comanda? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val error: String? = null,
    val pagada: Boolean = false
)

class ComandaViewModel(
    private val getActiveComandaUseCase: GetActiveComandaUseCase,
    private val pagarComandaUseCase: PagarComandaUseCase,
    private val removeDetalleUseCase: RemoveDetalleUseCase,
    private val repository: BarRepository
) : ViewModel() {

    private val _mesaId = MutableStateFlow<Long?>(null)
    private val _mesa = MutableStateFlow<Mesa?>(null)
    private val _isActionLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _pagada = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ComandaUiState> = combine(
        _mesaId.flatMapLatest { id ->
            if (id != null) getActiveComandaUseCase(id) else flowOf(null)
        },
        _mesa,
        _isActionLoading,
        _error,
        _pagada
    ) { comanda, mesa, isActionLoading, error, pagada ->
        ComandaUiState(
            mesa = mesa,
            comanda = comanda,
            isLoading = false,
            isActionLoading = isActionLoading,
            error = error,
            pagada = pagada
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ComandaUiState(isLoading = true)
    )

    fun setMesaId(mesaId: Long) {
        if (_mesaId.value != mesaId) {
            _mesaId.value = mesaId
            viewModelScope.launch {
                _mesa.value = repository.getMesa(mesaId)
                repository.syncComandasMesa(mesaId)
            }
        }
    }

    fun removeDetalle(detalleId: Long) {
        val mesaId = _mesaId.value ?: return
        val comandaId = uiState.value.comanda?.id ?: return

        viewModelScope.launch {
            _isActionLoading.value = true
            _error.value = null
            val result = removeDetalleUseCase(comandaId, detalleId, mesaId)
            if (!result.isSuccess) {
                _error.value = "Error eliminando: ${result.exceptionOrNull()?.message}"
            }
            _isActionLoading.value = false
        }
    }

    fun pagarComanda() {
        val comandaId = uiState.value.comanda?.id ?: return

        viewModelScope.launch {
            _isActionLoading.value = true
            _error.value = null
            val result = pagarComandaUseCase(comandaId)
            if (result.isSuccess) {
                _pagada.value = true
            } else {
                _error.value = "Error al pagar: ${result.exceptionOrNull()?.message}"
            }
            _isActionLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
