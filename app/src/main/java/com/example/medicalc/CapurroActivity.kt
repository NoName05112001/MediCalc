package com.example.medicalc

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CapurroActivity : AppCompatActivity() {

    private val spinnerIdsAndArrays = listOf(
        Pair(R.id.spinnerAuricular, R.array.opciones_auricular),
        Pair(R.id.spinnerGlandulaMamaria, R.array.opciones_glandula_mamaria),
        Pair(R.id.spinnerPezon, R.array.opciones_pezon),
        Pair(R.id.spinnerPiel, R.array.opciones_piel),
        Pair(R.id.spinnerPlantares, R.array.opciones_plantares)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_capurro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón de regresar al menú
        val btnInicio = findViewById<Button>(R.id.btn_inicio)
        btnInicio.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        val btnHistorial = findViewById<android.widget.Button>(R.id.btn_historial)
        btnHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        // Cargar todos los spinners
        spinnerIdsAndArrays.forEach { (spinnerId, arrayId) ->
            cargarSpinner(spinnerId, arrayId)
        }

        // Botón de calcular edad gestacional
        val btnCalcular = findViewById<Button>(R.id.btnCalcularEdad)
        btnCalcular.setOnClickListener {
            if (validarCampos()) {
                val edadGestacional = calcularEdadGestacionalCapurro()
                mostrarResultadoDialog(edadGestacional)
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

    private fun calcularEdadGestacionalCapurro(): Int {
        val puntajes = listOf(
            obtenerPuntaje(findViewById(R.id.spinnerAuricular)),
            obtenerPuntaje(findViewById(R.id.spinnerGlandulaMamaria)),
            obtenerPuntaje(findViewById(R.id.spinnerPezon)),
            obtenerPuntaje(findViewById(R.id.spinnerPiel)),
            obtenerPuntaje(findViewById(R.id.spinnerPlantares))
        )

        val suma = puntajes.sum()
        val edad = (204 + suma) / 7.0
        return edad.toInt() // edad en semanas, redondeada
    }

    private fun obtenerPuntaje(spinner: Spinner): Int {
        return when (spinner.id) {
            R.id.spinnerAuricular -> when (spinner.selectedItemPosition) {
                1 -> 8
                2 -> 16
                3 -> 24
                else -> 0
            }
            R.id.spinnerGlandulaMamaria -> when (spinner.selectedItemPosition) {
                1 -> 0
                2 -> 5
                3 -> 10
                4 -> 15
                else -> 0
            }
            R.id.spinnerPezon -> when (spinner.selectedItemPosition) {
                1 -> 0
                2 -> 5
                3 -> 10
                4 -> 15
                else -> 0
            }
            R.id.spinnerPiel -> when (spinner.selectedItemPosition) {
                1 -> 0
                2 -> 5
                3 -> 10
                4 -> 15
                5 -> 20
                else -> 0
            }
            R.id.spinnerPlantares -> when (spinner.selectedItemPosition) {
                1 -> 0
                2 -> 5
                3 -> 10
                4 -> 15
                5 -> 20
                else -> 0
            }
            else -> 0
        }
    }


    private fun mostrarResultadoDialog(semanas: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_resultado_capurro, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        val tvResultado = dialogView.findViewById<TextView>(R.id.tvResultadoEdad)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardar)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrar)

        tvResultado.text = "Edad Gestacional: $semanas semanas"
        btnGuardar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bgLogin)
        btnCerrar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bgLogin)

        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
        }

        btnGuardar.setOnClickListener {
            alertDialog.dismiss()
            mostrarDialogoNombre(semanas)
        }
    }
    private fun mostrarDialogoNombre(semanas: Int) {
        val nombreDialogView = layoutInflater.inflate(R.layout.nombre_paciente, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(nombreDialogView)
            .setCancelable(false)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        val etNombre = nombreDialogView.findViewById<EditText>(R.id.etNombreRecienNacido)
        val btnGuardar = nombreDialogView.findViewById<Button>(R.id.btnGuardar)
        val btnCancelar = nombreDialogView.findViewById<Button>(R.id.btnCancelar)
        val btnCerrar = nombreDialogView.findViewById<Button>(R.id.btnCerrar)

        // Aplicar color
        val colorFondo = ContextCompat.getColorStateList(this, R.color.bgLogin)
        btnGuardar.backgroundTintList = colorFondo
        btnCancelar.backgroundTintList = colorFondo
        btnCerrar.backgroundTintList = colorFondo

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa el nombre del recién nacido.", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                mostrarDialogoConfirmacion(nombre, semanas)
            }
        }

        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }

        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
        }
    }


    private fun mostrarDialogoConfirmacion(nombre: String, semanas: Int) {
        val confirmDialogView = layoutInflater.inflate(R.layout.guardado, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(confirmDialogView)
            .setCancelable(false)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        val tvEdad = confirmDialogView.findViewById<TextView>(R.id.tvResultadoEdad)
        val btnCerrar = confirmDialogView.findViewById<Button>(R.id.btnCerrar)

        tvEdad.text = "Resultado de $nombre guardado en el historial.\nEdad gestacional: $semanas semanas"

        // Aplicar color
        btnCerrar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bgLogin)

        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
        }
    }




}
