package com.ecomarket.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class ProfileUiState(
    val profileImageUri: Uri? = null
)

class ProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun onProfileImageChange(uri: Uri?) {
        uiState = uiState.copy(profileImageUri = uri)
    }
}