package com.example.medicalc

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DubowitzActivity : AppCompatActivity() {

    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var tvTituloGenitales: TextView
    private lateinit var spinnerGenitales: Spinner
    private lateinit var spinnerPostura: Spinner
    private lateinit var spinnerAnguloPopliteo: Spinner
    private lateinit var spinnerTono: Spinner
    private lateinit var spinnerPiel: Spinner
    private lateinit var spinnerPlieguesPlantares: Spinner
    private lateinit var btnCalcular: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dubowitz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        tvTituloGenitales = findViewById(R.id.tvTituloGenitales)
        spinnerGenitales = findViewById(R.id.spinnerGenitales)
        spinnerPostura = findViewById(R.id.spinnerPostura)
        spinnerAnguloPopliteo = findViewById(R.id.spinnerAnguloPopliteo)
        spinnerTono = findViewById(R.id.spinnerTono)
        spinnerPiel = findViewById(R.id.spinnerPielDubowitz)
        spinnerPlieguesPlantares = findViewById(R.id.spinnerPlantaresDubowits)
        btnCalcular = findViewById(R.id.btnCalcularEdad)

        // Configurar spinners
        setupSpinners()

        // Listener para género
        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            updateGenitalesSection(checkedId)
        }
        updateGenitalesSection(genderRadioGroup.checkedRadioButtonId)

        // Botones de navegación
        findViewById<Button>(R.id.btn_inicio).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
        findViewById<Button>(R.id.btn_historial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        // Botón calcular
        btnCalcular.setOnClickListener {
            if (validarCampos()) {
                val puntaje = calcularPuntaje()
                val edadGestacional = calcularEdadGestacional(puntaje)
                mostrarResultadoDialog(edadGestacional)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinners() {
        setupSpinner(spinnerPostura, R.array.postura_opciones)
        setupSpinner(spinnerAnguloPopliteo, R.array.angulo_popliteo_opciones)
        setupSpinner(spinnerTono, R.array.tono_opciones)
        setupSpinner(spinnerPiel, R.array.piel_opciones)
        setupSpinner(spinnerPlieguesPlantares, R.array.pliegues_plantares_opciones)
    }

    private fun setupSpinner(spinner: Spinner, arrayResId: Int) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            arrayResId,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun updateGenitalesSection(checkedId: Int) {
        when (checkedId) {
            R.id.radioMale -> {
                tvTituloGenitales.text = "Genitales Masculinos"
                setupSpinner(spinnerGenitales, R.array.genitales_masculinos)
            }
            R.id.radioFemale -> {
                tvTituloGenitales.text = "Genitales Femeninos"
                setupSpinner(spinnerGenitales, R.array.genitales_femeninos)
            }
            else -> {
                tvTituloGenitales.text = "Genitales"
                spinnerGenitales.adapter = null
            }
        }
    }

    private fun validarCampos(): Boolean {
        return spinnerGenitales.selectedItemPosition > 0 &&
                spinnerPostura.selectedItemPosition > 0 &&
                spinnerAnguloPopliteo.selectedItemPosition > 0 &&
                spinnerTono.selectedItemPosition > 0 &&
                spinnerPiel.selectedItemPosition > 0 &&
                spinnerPlieguesPlantares.selectedItemPosition > 0
    }

    private fun calcularPuntaje(): Int {
        var puntaje = 0

        puntaje += when (spinnerGenitales.selectedItem.toString()) {
            "Testículos no descendidos", "Clítoris prominente" -> 0
            "Testículos en canal inguinal", "Labios menores prominentes" -> 1
            "Testículos descendidos", "Labios mayores pequeños" -> 2
            "Escroto liso", "Labios mayores cubriendo menores" -> 3
            "Escroto con pliegues", "Labios mayores cubriendo completamente" -> 4
            else -> 0
        }

        puntaje += spinnerPostura.selectedItemPosition - 1
        puntaje += spinnerAnguloPopliteo.selectedItemPosition - 1
        puntaje += spinnerTono.selectedItemPosition - 1
        puntaje += spinnerPiel.selectedItemPosition - 1
        puntaje += spinnerPlieguesPlantares.selectedItemPosition - 1

        return puntaje
    }

    private fun calcularEdadGestacional(puntaje: Int): Int {
        // Fórmula aproximada (puedes ajustarla si tienes los datos reales)
        return (27 + (puntaje * 0.5)).toInt()
    }

    private fun mostrarResultadoDialog(semanas: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_resultado_capurro, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()

        val tvResultado = dialogView.findViewById<TextView>(R.id.tvResultadoEdad)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardar)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrar)

        tvResultado.text = "Edad Gestacional: $semanas semanas"
        btnGuardar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bgLogin)
        btnCerrar.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bgLogin)

        btnGuardar.setOnClickListener {
            Toast.makeText(this, "Resultado guardado (por implementar)", Toast.LENGTH_SHORT).show()
        }

        btnCerrar.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}
