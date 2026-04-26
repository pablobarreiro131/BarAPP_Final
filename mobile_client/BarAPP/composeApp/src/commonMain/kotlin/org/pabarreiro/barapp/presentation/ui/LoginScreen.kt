package org.pabarreiro.barapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.pabarreiro.barapp.presentation.ui.theme.*
import org.pabarreiro.barapp.presentation.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().widthIn(max = 400.dp)
        ) {
            Text(
                text = "BAR",
                style = LuxuryTypography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = PrimaryIvory
            )
            Text(
                text = "APP",
                style = LuxuryTypography.headlineLarge.copy(fontWeight = FontWeight.Light),
                color = TextMuted,
                modifier = Modifier.offset(y = (-8).dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryIvory,
                unfocusedBorderColor = BorderStrong,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryIvory,
                focusedLabelColor = PrimaryIvory,
                unfocusedLabelColor = TextMuted
            )
            
            val textFieldShape = RoundedCornerShape(0.dp)
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico", style = LuxuryTypography.bodyLarge) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = textFieldShape,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", style = LuxuryTypography.bodyLarge) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = textFieldShape,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryIvory,
                    contentColor = BgDark,
                    disabledContainerColor = PrimaryIvoryMuted,
                    disabledContentColor = BgDark
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BgDark, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = "ENTRAR",
                        style = LuxuryTypography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            uiState.error?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Text(it, color = AccentRed, style = LuxuryTypography.bodyLarge, textAlign = TextAlign.Center)
            }
        }
    }
}
