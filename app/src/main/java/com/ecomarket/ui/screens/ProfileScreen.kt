package com.ecomarket.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.ecomarket.profile.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    vm: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = vm.uiState

    // 1. Creamos una URI temporal para guardar la foto
    //    Se usa `remember` para que no se cree una nueva en cada recomposición.
    val tempUri = remember { mutableStateOf<Uri?>(null) }

    // 2. Creamos el "launcher" que abrirá la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                // Si la foto se tomó correctamente, actualizamos el ViewModel con la URI guardada
                vm.onProfileImageChange(tempUri.value)
            }
        }
    )

    // 3. Creamos el "launcher" que pedirá el permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                // Si el permiso fue otorgado, creamos una nueva URI y lanzamos la cámara
                val newUri = context.createImageUri()
                tempUri.value = newUri // Guardamos la URI para usarla en el resultado
                cameraLauncher.launch(newUri)
            } else {
                // Opcional: Mostrar un mensaje si el permiso fue denegado
                Log.e("ProfileScreen", "Permiso de cámara denegado")
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 4. Mostramos la imagen de perfil usando la URI del ViewModel
        //    Usamos la librería Coil, que ya tienes en el proyecto (`AsyncImage`).
        Image(
            painter = rememberAsyncImagePainter(
                model = uiState.profileImageUri ?: "https://via.placeholder.com/150" // Placeholder
            ),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(24.dp))

        // 5. El botón que inicia todo el proceso
        Button(
            onClick = {
                // Al hacer clic, pedimos el permiso de cámara.
                // El resto del flujo continúa en el `onResult` del `permissionLauncher`.
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        ) {
            Text("Tomar Foto de Perfil")
        }
    }
}

// Función de ayuda para crear una URI segura y única para la nueva foto
private fun Context.createImageUri(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = filesDir // Directorio privado de la app
    val image = File.createTempFile(
        imageFileName, ".jpg", storageDir
    )
    return FileProvider.getUriForFile(
        this,
        "${this.packageName}.provider", // Debe coincidir con el `authorities` del AndroidManifest
        image
    )
}