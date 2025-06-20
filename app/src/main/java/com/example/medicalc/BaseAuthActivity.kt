package com.example.medicalc

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Clase base abstracta para actividades que requieren que el usuario esté autenticado.
 * Si el usuario no está logueado, redirige a MainActivity (Login).
 */
abstract class BaseAuthActivity : AppCompatActivity() {

    protected lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager() // Inicializa AuthManager

        // --- Lógica de verificación de sesión ---
        if (!authManager.isUserLoggedIn()) {
            // Si el usuario NO está logueado, redirige a MainActivity (Login)
            val intent = Intent(this, MainActivity::class.java) // Asume MainActivity es tu pantalla de login
            // Estas flags son cruciales para limpiar la pila de actividades
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Finaliza la actividad actual
        }
        // Si el usuario está logueado, onCreate continuará ejecutándose en la subclase.
    }
}