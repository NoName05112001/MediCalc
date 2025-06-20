package com.example.medicalc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast // Para mostrar mensajes al usuario
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope // Para usar coroutines
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // Instancia de AuthManager para manejar la autenticación
    private lateinit var authManager: AuthManager

    // Declaración de las vistas del layout
    private lateinit var editTextNombre: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register) // Asegúrate que apunta a tu XML de registro

        // Configuración de insets para el diseño de borde a borde
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar AuthManager
        authManager = AuthManager()

        // Conectar las vistas del XML con las variables en Kotlin
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextEmail = findViewById(R.id.editTextTextEmailAddress) // ID del email
        editTextPassword = findViewById(R.id.editTextTextPassword) // ID de la contraseña
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin) // El botón para ir a iniciar sesión si ya tiene cuenta

        // Configurar el listener para el botón de registro
        btnRegister.setOnClickListener {
            registerUser()
        }

        // Configurar el listener para el botón de ir a inicio de sesión
        btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Asumiendo que MainActivity es tu pantalla de login
            startActivity(intent)
            finish() // Cierra RegisterActivity para que el usuario no pueda volver con el botón atrás
        }
    }

    /**
     * Función para manejar la lógica de registro de usuario.
     */
    private fun registerUser() {
        val name = editTextNombre.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        // Validaciones básicas de entrada
        if (name.isEmpty()) {
            editTextNombre.error = "El nombre es requerido"
            editTextNombre.requestFocus()
            return
        }
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
        if (password.length < 6) { // Firebase Auth requiere al menos 6 caracteres para la contraseña
            editTextPassword.error = "La contraseña debe tener al menos 6 caracteres"
            editTextPassword.requestFocus()
            return
        }

        // Iniciar la coroutine para el registro
        lifecycleScope.launch {
            val success = authManager.signUp(name, email, password)

            if (success) {
                // Registro exitoso: Mostrar mensaje y redirigir
                Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, MenuActivity::class.java) // Redirige a tu pantalla principal/dashboard
                startActivity(intent)
                finish() // Cierra RegisterActivity para que el usuario no pueda volver
            } else {
                // Registro fallido: Mostrar mensaje de error
                // Puedes obtener errores más específicos de Firebase para dar un mejor feedback
                Toast.makeText(this@RegisterActivity, "Error en el registro. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}