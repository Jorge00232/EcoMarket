package com.ecomarket.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.ecomarket.di.Graph
import com.ecomarket.navigation.Routes
import com.ecomarket.profile.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    vm: ProfileViewModel = viewModel(),
    navController: androidx.navigation.NavController
) {
    val uiState = vm.uiState
    val context = LocalContext.current

    // --- Lógica para la cámara ---
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) vm.onProfileImageChange(tempUri.value) }
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                val newUri = context.createImageUri()
                tempUri.value = newUri
                cameraLauncher.launch(newUri)
            }
        }
    )

    // --- Efecto para los mensajes Toast ---
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    // --- UI de la pantalla ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        // --- Imagen de Perfil ---
        if (uiState.profileImageUri != null) {
            // Si tenemos una URI, mostramos la foto tomada
            Image(
                painter = rememberAsyncImagePainter(model = uiState.profileImageUri),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { permissionLauncher.launch(Manifest.permission.CAMERA) },
                contentScale = ContentScale.Crop
            )
        } else {
            // Si no, mostramos el ícono por defecto
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Ícono de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clickable { permissionLauncher.launch(Manifest.permission.CAMERA) },
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text("Toca la imagen para cambiarla", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(24.dp))

        // --- Formulario de Datos ---
        OutlinedTextField(value = uiState.email, onValueChange = {}, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = uiState.name, onValueChange = vm::onNameChange, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = uiState.birthDate, onValueChange = vm::onBirthDateChange, label = { Text("Fecha de Nacimiento (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = uiState.shippingAddress, onValueChange = vm::onShippingAddressChange, label = { Text("Dirección de Envío") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = uiState.newPassword, onValueChange = vm::onNewPasswordChange, label = { Text("Nueva Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(24.dp))

        // --- Botones ---
        Button(onClick = { vm.saveProfileChanges() }, modifier = Modifier.fillMaxWidth()) { Text("Guardar Cambios") }
        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = {
                Graph.logout()
                navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar Sesión") }
    }
}

// --- Función de Ayuda para la URI ---
private fun Context.createImageUri(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = filesDir
    val image = File.createTempFile(imageFileName, ".jpg", storageDir)
    return FileProvider.getUriForFile(this, "${this.packageName}.provider", image)
}