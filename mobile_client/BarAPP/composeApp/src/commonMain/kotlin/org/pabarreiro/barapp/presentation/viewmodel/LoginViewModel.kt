package org.pabarreiro.barapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.pabarreiro.barapp.domain.repository.BarRepository

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val repository: BarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    
    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            if (repository.hasSession()) {
                println("[BarApp] [LoginViewModel] Autologin exitoso")
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            println("[BarApp] [LoginViewModel] Intentando login para: $email")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.login(email, password)
            if (result.isSuccess) {
                println("[BarApp] [LoginViewModel] Login exitoso para: $email")
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
                println("[BarApp] [LoginViewModel] Error en login para $email: $errorMessage")
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    error = errorMessage
                )
            }
        }
    }
}
