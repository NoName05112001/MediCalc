package com.example.medicalc

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast // Para mostrar mensajes al usuario
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope // Para usar coroutines
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    // Declaración de las vistas del layout para el login
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login) // Asegúrate que apunta a tu XML de login

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar AuthManager
        authManager = AuthManager()

        // Validación para redireccionar
        if (authManager.isUserLoggedIn()) {
            // Si el usuario ya está logueado, redirige directamente al menú principal
            val intent = Intent(this, MenuActivity::class.java)
            // Estas flags son cruciales para limpiar la pila de actividades
            // y evitar que el usuario regrese a MainActivity con el botón de atrás
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Cierra MainActivity para que no quede en el stack
            return // Salir de onCreate para no ejecutar el resto del código
        }

        // Conectar las vistas del XML con las variables en Kotlin
        editTextEmail = findViewById(R.id.editTextTextEmailAddress) // ID del email
        editTextPassword = findViewById(R.id.editTextTextPassword) // ID de la contraseña
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // --- Lógica de verificación inicial de sesión ---
        // Verifica si ya hay una sesión activa al iniciar la app
        if (authManager.isUserLoggedIn()) {
            // Si el usuario ya está logueado, redirige directamente al menú principal
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Cierra MainActivity para que no pueda volver con el botón atrás
            return // Sale de onCreate para no ejecutar el resto del código
        }
        // --- Fin de la verificación inicial ---

        // Configurar el listener para el botón de inicio de sesión
        btnLogin.setOnClickListener {
            loginUser()
        }

        // Configurar el listener para el botón de ir a registro
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            // No llamamos a finish() aquí, para que el usuario pueda volver a la pantalla de login
        }
    }

    /**
     * Función para manejar la lógica de inicio de sesión del usuario.
     */
    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        // Validaciones básicas de entrada
        if (email.isEmpty()) {
            editTextEmail.error = "El email es requerido"
            editTextEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextPassword.error = "La contraseña es requerida"
            editTextPassword.requestFocus()
            return
        }

        // Iniciar la coroutine para el inicio de sesión
        lifecycleScope.launch {
            val success = authManager.signIn(email, password)

            if (success) {
                // Inicio de sesión exitoso. Ahora intentamos obtener el nombre del usuario.
                val currentUserUid = authManager.getCurrentUserUid()
                var userName = "usuario" // Valor por defecto si no se puede obtener el nombre

                if (currentUserUid != null) {
                    val userProfile = authManager.getUserProfile(currentUserUid)
                    if (userProfile != null && userProfile.name.isNotEmpty()) {
                        userName = userProfile.name
                    }
                }

                // Crear y mostrar el Toast personalizado
                val toastMessage = "¡Bienvenido, $userName!"
                val toast = Toast.makeText(this@MainActivity, toastMessage, Toast.LENGTH_SHORT)

                // Posicionar el Toast en el centro
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                //Toast.makeText(this@MainActivity, "¡Bienvenido, $userName!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@MainActivity, MenuActivity::class.java) // Redirige a tu pantalla principal/menú
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Limpia la pila
                startActivity(intent)
                finish() // Cierra MainActivity
            } else {
                // Inicio de sesión fallido: Mostrar mensaje de error
                Toast.makeText(this@MainActivity, "Error de inicio de sesión. Verifica tus credenciales.", Toast.LENGTH_SHORT).show()
                // Aquí podrías añadir lógica para errores específicos de Firebase (ej. usuario no encontrado, contraseña incorrecta)
            }
        }
    }
}