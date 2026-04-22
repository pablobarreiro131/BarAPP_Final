package org.pabarreiro.barapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.pabarreiro.barapp.domain.model.Mesa
import org.pabarreiro.barapp.domain.repository.BarRepository
import org.pabarreiro.barapp.domain.usecase.GetTables

sealed interface TablesUiState {
    data object Loading : TablesUiState
    data class Success(val mesas: List<Mesa>) : TablesUiState
    data class Error(val message: String) : TablesUiState
}

class TablesViewModel(
    private val getTables: GetTables,
    private val repository: BarRepository
) : ViewModel() {

    val uiState: StateFlow<TablesUiState> = getTables()
        .map { TablesUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TablesUiState.Loading
        )

    init {
        println("[BarApp] [TablesViewModel] Inicializando TablesViewModel")
        refreshTables()
    }
    
    fun refreshTables() {
        viewModelScope.launch {
            println("[BarApp] [TablesViewModel] Sincronizando mesas con el servidor...")
            val result = repository.syncMesas()
            if (result.isSuccess) {
                println("[BarApp] [TablesViewModel] Sincronización de mesas exitosa")
            } else {
                println("[BarApp] [TablesViewModel] Error sincronizando mesas: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}
