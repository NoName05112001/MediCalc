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

class MadurezCutaneaActivity : AppCompatActivity() {

    private val spinnerIdsAndArrays = listOf(
        Pair(R.id.spinnerTexturaPiel, R.array.textura_piel_opciones),
        Pair(R.id.spinnerGrosorPiel, R.array.grosor_piel_opciones),
        Pair(R.id.spinnerTransparenciaPiel, R.array.transparencia_piel_opciones),
        Pair(R.id.spinnerDescamacionPiel, R.array.descamacion_piel_opciones)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_madurez_cutanea)

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

        // Cargar los spinners
        spinnerIdsAndArrays.forEach { (spinnerId, arrayId) ->
            cargarSpinner(spinnerId, arrayId)
        }

        val btnCalcular = findViewById<Button>(R.id.btnEvaluarMadurezcutanea)
        btnCalcular.setOnClickListener {
            if (validarCampos()) {
                val edadGestacional = calcularEdadGestacionalCutanea()
                val descripcion = obtenerDescripcionMadurez(edadGestacional)
                mostrarResultadoDialog(descripcion, edadGestacional)
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

    private fun calcularEdadGestacionalCutanea(): Int {
        val puntajes = listOf(
            obtenerPuntaje(findViewById(R.id.spinnerTexturaPiel)),
            obtenerPuntaje(findViewById(R.id.spinnerGrosorPiel)),
            obtenerPuntaje(findViewById(R.id.spinnerTransparenciaPiel)),
            obtenerPuntaje(findViewById(R.id.spinnerDescamacionPiel))
        )
        return (puntajes.sum() + 20) // Fórmula base (ajustable)
    }

    private fun obtenerPuntaje(spinner: Spinner): Int {
        return when (spinner.id) {
            R.id.spinnerTexturaPiel -> when (spinner.selectedItemPosition) {
                1 -> 2
                2 -> 4
                3 -> 6
                4 -> 8
                5 -> 10
                6 -> 12
                else -> 0
            }
            R.id.spinnerGrosorPiel -> when (spinner.selectedItemPosition) {
                1 -> 2
                2 -> 4
                3 -> 6
                4 -> 8
                5 -> 10
                6 -> 12
                else -> 0
            }
            R.id.spinnerTransparenciaPiel -> when (spinner.selectedItemPosition) {
                1 -> 2
                2 -> 4
                3 -> 6
                4 -> 8
                5 -> 10
                6 -> 12
                else -> 0
            }
            R.id.spinnerDescamacionPiel -> when (spinner.selectedItemPosition) {
                1 -> 2
                2 -> 4
                3 -> 6
                4 -> 8
                5 -> 10
                6 -> 12
                else -> 0
            }
            else -> 0
        }
    }

    private fun obtenerDescripcionMadurez(edadSemanas: Int): String {
        val descripcion = when {
            edadSemanas < 28 -> "Extremadamente inmadura"
            edadSemanas in 28..31 -> "Muy inmadura"
            edadSemanas in 32..34 -> "Moderadamente inmadura"
            edadSemanas in 35..36 -> "Levemente inmadura"
            edadSemanas in 37..38 -> "Madura"
            edadSemanas >= 39 -> "Muy madura"
            else -> "Desconocida"
        }
        return "Madurez Cutánea Estimada:\n$descripcion (aproximadamente $edadSemanas semanas)"
    }

    private fun mostrarResultadoDialog(resultadoTexto: String, semanas: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_resultado_madurez_cutanea, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        val tvTitulo = dialogView.findViewById<TextView>(R.id.tvTitulo)
        val tvResultado = dialogView.findViewById<TextView>(R.id.tvResultadoEdad)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardar)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrar)

        tvTitulo.text = "Resultado de la Evaluación de Madurez Cutánea"
        tvResultado.text = resultadoTexto

        btnGuardar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bgLogin)
        btnCerrar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bgLogin)

        btnGuardar.setOnClickListener {
            alertDialog.dismiss()
            mostrarDialogoNombre(semanas)
        }

        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
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
