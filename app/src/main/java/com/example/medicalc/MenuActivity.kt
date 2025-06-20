package com.example.medicalc

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MenuActivity : BaseAuthActivity() {
    private lateinit var userNameTextView: TextView // Declarar el TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Conectar el TextView del layout
        userNameTextView = findViewById(R.id.userNameText)

        // --- Lógica para mostrar el nombre del usuario ---
        displayUserName()

        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            authManager.signOut()
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        val btnHistorial = findViewById<android.widget.Button>(R.id.btn_historial)
        btnHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        val linearCapurro = findViewById<LinearLayout>(R.id.linearCapurro)
        linearCapurro.setOnClickListener {
            val intent = Intent(this, CapurroActivity::class.java)
            startActivity(intent)
        }

        val linearBallard = findViewById<LinearLayout>(R.id.linearBallard)
        linearBallard.setOnClickListener {
            val intent = Intent(this, DubowitzActivity::class.java)
            startActivity(intent)
        }

        val linearMadurezCutanea = findViewById<LinearLayout>(R.id.linearMadurezCutanea)
        linearMadurezCutanea.setOnClickListener {
            val intent = Intent(this, MadurezCutaneaActivity::class.java)
            startActivity(intent)
        }
    }
    /**
     * Función para obtener y mostrar el nombre del usuario logueado.
     */
    private fun displayUserName() {
        userNameTextView.text = "usuario"
        val currentUserUid = authManager.getCurrentUserUid()

        if (currentUserUid != null) {
            // Lanza una coroutine para llamar a la función suspend de AuthManager
            lifecycleScope.launch {
                val userProfile = authManager.getUserProfile(currentUserUid)
                if (userProfile != null && userProfile.name.isNotEmpty()) {
                    userNameTextView.text = "${userProfile.name}"
                }
            }
        }
    }
}
