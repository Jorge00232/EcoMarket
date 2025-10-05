package com.ecomarket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecomarket.auth.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGuest: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val ui = vm.ui

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EcoMarket", style = MaterialTheme.typography.headlineMedium)
        Text("Inicia sesión o entra como invitado", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = ui.email,
            onValueChange = vm::onEmailChange,
            label = { Text("Correo electrónico") },
            isError = ui.emailError != null,
            supportingText = { ui.emailError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = ui.password,
            onValueChange = vm::onPasswordChange,
            label = { Text("Contraseña") },
            isError = ui.passwordError != null,
            supportingText = { ui.passwordError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (ui.isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = vm::togglePasswordVisibility) {
                    Icon(
                        imageVector = if (ui.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (ui.isPasswordVisible) "Ocultar" else "Mostrar"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    vm.submit(onSuccess = onLoginSuccess)
                }
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { vm.submit(onSuccess = onLoginSuccess) },
            enabled = ui.isValid && !ui.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (ui.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(18.dp))
            } else {
                Text("Iniciar sesión")
            }
        }

        Spacer(Modifier.height(12.dp))

        ElevatedButton(
            onClick = { vm.loginAsGuest(onSuccess = onGuest) },
            enabled = !ui.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar como invitado")
        }
    }
}
