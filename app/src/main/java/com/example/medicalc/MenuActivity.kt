package com.example.medicalc

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
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

        val linearligyhowie = findViewById<LinearLayout>(R.id.linearLigginsyHowie)
        linearligyhowie.setOnClickListener {
            val intent = Intent(this, LigginsYHowieActivity::class.java)
            startActivity(intent)
        }

    }
}
