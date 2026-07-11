package com.example.medicalc

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HistorialActivity : BaseAuthActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        findViewById<android.widget.Button>(R.id.btn_inicio).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        cargarHistorial()
    }

    private fun cargarHistorial() {
        val layout = findViewById<LinearLayout>(R.id.layoutHistorial)

        lifecycleScope.launch {
            val calculos = authManager.obtenerCalculos()

            if (calculos.isEmpty()) {
                val tvVacio = TextView(this@HistorialActivity).apply {
                    text = "No hay cálculos guardados aún."
                    textSize = 14f
                    setTextColor(Color.GRAY)
                    gravity = Gravity.CENTER
                    setPadding(0, 32, 0, 0)
                }
                layout.addView(tvVacio)
                return@launch
            }

            calculos.forEach { calculo ->
                val docId = calculo["_id"] as? String ?: ""
                val tipo = calculo["tipo"] as? String ?: ""
                val nombre = calculo["nombrePaciente"] as? String ?: ""
                val resultado = calculo["resultado"] as? String ?: ""
                val fecha = calculo["fecha"] as? com.google.firebase.Timestamp
                val fechaTexto = if (fecha != null) {
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                    sdf.format(fecha.toDate())
                } else ""

                @Suppress("UNCHECKED_CAST")
                val campos = calculo["campos"] as? Map<String, Any> ?: emptyMap()
                val card = crearTarjeta(docId, tipo, nombre, resultado, fechaTexto, campos, layout)
                layout.addView(card)
            }
        }
    }

    private fun crearTarjeta(
        docId: String,
        tipo: String,
        nombre: String,
        resultado: String,
        fechaTexto: String,
        campos: Map<String, Any>,
        layout: LinearLayout
    ): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 20, 24, 20)
            setBackgroundColor(Color.parseColor("#0D9488"))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16)
            layoutParams = params
        }

        // Fila superior: tipo + botón 3 puntos
        val filaTop = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val tvTipo = TextView(this).apply {
            text = "Test: $tipo"
            textSize = 14f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val tvMenu = TextView(this).apply {
            text = "⋮"
            textSize = 20f
            setTextColor(Color.WHITE)
            setPadding(16, 0, 0, 0)
        }

        filaTop.addView(tvTipo)
        filaTop.addView(tvMenu)

        val tvNombre = TextView(this).apply {
            text = "Paciente: $nombre"
            textSize = 14f
            setTextColor(Color.WHITE)
        }

        val tvResultado = TextView(this).apply {
            text = resultado
            textSize = 13f
            setTextColor(Color.WHITE)
        }

        val tvFecha = TextView(this).apply {
            text = fechaTexto
            textSize = 11f
            setTextColor(Color.parseColor("#B2DFDB"))
        }

        card.addView(filaTop)
        card.addView(tvNombre)
        card.addView(tvResultado)
        card.addView(tvFecha)

        tvMenu.setOnClickListener { anchor ->
            val popup = PopupMenu(this, anchor)
            popup.menu.add(0, 1, 0, "Editar")
            popup.menu.add(0, 2, 1, "Eliminar")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        abrirEdicion(docId, tipo, nombre, campos)
                        true
                    }
                    2 -> {
                        lifecycleScope.launch {
                            val ok = authManager.eliminarCalculo(docId)
                            if (ok) {
                                layout.removeView(card)
                                Toast.makeText(this@HistorialActivity, "Registro eliminado", Toast.LENGTH_SHORT).show()
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        return card
    }

    private fun abrirEdicion(docId: String, tipo: String, nombrePaciente: String, campos: Map<String, Any>) {
        val activityClass = when (tipo) {
            "Capurro" -> CapurroActivity::class.java
            "Dubowitz" -> DubowitzActivity::class.java
            "Madurez Cutánea" -> MadurezCutaneaActivity::class.java
            "Liggins y Howie" -> LigginsYHowieActivity::class.java
            else -> return
        }
        val intent = Intent(this, activityClass).apply {
            putExtra("EDIT_DOCUMENT_ID", docId)
            putExtra("EDIT_NOMBRE_PACIENTE", nombrePaciente)
            campos.forEach { (k, v) ->
                when (v) {
                    is Long -> putExtra("CAMPO_$k", v.toInt())
                    is Int -> putExtra("CAMPO_$k", v)
                    is String -> putExtra("CAMPO_$k", v)
                }
            }
        }
        startActivity(intent)
    }
}
