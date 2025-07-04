package com.example.medicalc // Asegúrate de que este sea el paquete correcto de tu app

/**
 * Clase de datos que representa el perfil de un usuario en Firestore.
 */
data class UserProfile(
    val name: String = "",
    val email: String = ""
)