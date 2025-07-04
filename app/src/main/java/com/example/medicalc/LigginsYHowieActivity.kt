package com.example.medicalc

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LigginsYHowieActivity : AppCompatActivity() {

    private val spinnerIdsAndArrays = listOf(
        Pair(R.id.spinnerEsfuerzoRespiratorio, R.array.opciones_esfuerzo_respiratorio),
        Pair(R.id.spinnerRetraccionCostal, R.array.opciones_retraccion_costal),
        Pair(R.id.spinnerQuejidoRespiratorio, R.array.opciones_quejido_respiratorio),
        Pair(R.id.spinnerCianosis, R.array.opciones_cianosis),
        Pair(R.id.spinnerFrecuenciaRespiratoria, R.array.opciones_frecuencia_respiratoria),
        Pair(R.id.spinnerCapacidaddesucción, R.array.opciones_capacidad_de_succión)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_liggins_yhowie)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnInicio = findViewById<Button>(R.id.btn_inicio)
        btnInicio.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnHistorial = findViewById<Button>(R.id.btn_historial)
        btnHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        spinnerIdsAndArrays.forEach { (spinnerId, arrayId) ->
            cargarSpinner(spinnerId, arrayId)
        }

        val btnCalcular = findViewById<Button>(R.id.btnEvaluarMadurezPulmonar)
        btnCalcular.setOnClickListener {
            if (validarCampos()) {
                val puntaje = evaluarMadurezPulmonar()
                mostrarDialogoResultado(puntaje)
            }
        }
    }

    private fun cargarSpinner(spinnerId: Int, arrayId: Int) {
        val spinner = findViewById<Spinner>(spinnerId)
        val opciones = resources.getStringArray(arrayId)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun validarCampos(): Boolean {
        for ((spinnerId, _) in spinnerIdsAndArrays) {
            val spinner = findViewById<Spinner>(spinnerId)
            if (spinner.selectedItem.toString() == "Seleccionar...") {
                Toast.makeText(this, "Debes completar todos los campos del Test", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun evaluarMadurezPulmonar(): Int {
        var puntajeTotal = 0
        spinnerIdsAndArrays.forEach { (spinnerId, _) ->
            val spinner = findViewById<Spinner>(spinnerId)
            val posicion = spinner.selectedItemPosition
            puntajeTotal += posicion - 1  // "Seleccionar..." está en posición 0
        }
        return puntajeTotal
    }

    private fun mostrarDialogoResultado(puntaje: Int) {
        val interpretacion = when (puntaje) {
            in 0..5 -> "Sin dificultad respiratoria"
            in 6..10 -> "Dificultad respiratoria leve"
            in 11..15 -> "Dificultad respiratoria moderada"
            in 16..20 -> "Dificultad respiratoria grave"
            else -> "Muy grave – Riesgo vital"
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_resultado_ligyhowie, null)

        val tvPuntaje = dialogView.findViewById<TextView>(R.id.tvPuntajeTotal)
        val tvInterpretacion = dialogView.findViewById<TextView>(R.id.tvInterpretacion)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrarLiggins)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardarLiggins)

        tvPuntaje.text = "Puntaje total: $puntaje"
        tvInterpretacion.text = "Interpretación: $interpretacion"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnCerrar.setOnClickListener { dialog.dismiss() }

        btnGuardar.setOnClickListener {
            dialog.dismiss()
            mostrarDialogoNombreLiggins(puntaje, interpretacion)
        }

        dialog.show()
    }

    private fun mostrarDialogoNombreLiggins(puntaje: Int, interpretacion: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.nombre_paciente, null)

        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreRecienNacido)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardar)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrar)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val color = ContextCompat.getColorStateList(this, R.color.bgLogin)
        btnGuardar.backgroundTintList = color
        btnCancelar.backgroundTintList = color
        btnCerrar.backgroundTintList = color

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa el nombre del recién nacido.", Toast.LENGTH_SHORT).show()
            } else {
                dialog.dismiss()
                mostrarDialogoConfirmacionLiggins(nombre, puntaje, interpretacion)
            }
        }

        btnCancelar.setOnClickListener { dialog.dismiss() }
        btnCerrar.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun mostrarDialogoConfirmacionLiggins(nombre: String, puntaje: Int, interpretacion: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.guardado, null)

        val tvConfirmacion = dialogView.findViewById<TextView>(R.id.tvResultadoEdad)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrar)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        tvConfirmacion.text = "Resultado de $nombre guardado en el historial.\n" +
                "Puntaje: $puntaje\n" +
                "Interpretación: $interpretacion"

        btnCerrar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bgLogin)
        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
