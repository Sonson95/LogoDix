package com.example.logodix

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ElegirPalabraRealActivity : AppCompatActivity() {

    //variable para conexion de palabrasDAO
    private lateinit var palabrasDAO: PalabrasDAO
    private lateinit var puntuacionesDAO: PuntuacionesDAO

    // Componentes de la interfaz
    private lateinit var txtOpcion1: TextView
    private lateinit var txtOpcion2: TextView
    private lateinit var btnSiguiente: Button
    private lateinit var txtPuntuacion: TextView
    private lateinit var btnAtras: Button

    private var pares: MutableList<PalabrasDAO.ParPalabra> = mutableListOf()

    private var parActual: PalabrasDAO.ParPalabra? = null

    private var haRespondido = false

    private var puntuacion = 0


    //id de la actividad 2 Escoger palabras
    private val actividadId = 2
    private var idUsuario = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_elegir_palabra_real)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Instanciar DBHelper y DAO
        val dbHelper = DBHelper(this)
        palabrasDAO = PalabrasDAO(dbHelper)
        puntuacionesDAO = PuntuacionesDAO(this)

        // Solo insertar pseudopalabras si la tabla está vacía
        if (palabrasDAO.obtenerTodasPseudopalabras().isEmpty()) {
            palabrasDAO.insertarPseudopalabrasPredefinidas()
        } else {
            palabrasDAO.resetearPseudopalabras()
        }

        // Referencias de los componentes
        txtOpcion1 = findViewById(R.id.txtOpcion1)
        txtOpcion2 = findViewById(R.id.txtOpcion2)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        txtPuntuacion = findViewById(R.id.txtPuntuacion)
        btnAtras = findViewById(R.id.btnAtras)

        idUsuario = intent.getIntExtra("ID_USUARIO", 0)


        //Muestra puntuación inicial
        actualizarPuntuacionUI()

        txtOpcion1.setOnClickListener { verificarOpcion(txtOpcion1.text.toString(), txtOpcion1) }
        txtOpcion2.setOnClickListener { verificarOpcion(txtOpcion2.text.toString(), txtOpcion2) }

        btnSiguiente.setOnClickListener {
            onClickSiguiente()
        }
        btnAtras.setOnClickListener {
            finish()
        }


        // Carga palabras no usadas y barajarlas
        pares = palabrasDAO.obtenerPseudopalabrasSinUsar("facil").toMutableList()
        pares.shuffle()
        // Mostrar la primera pareja
        if (pares.isEmpty()) {
            mostrarResultadosFinales()
        } else {
            mostrarNuevoPar()
        }

    }


    //Función para obtener un nuevo par aleatorio y lo muestra en pantalla con posiciones mezcladas.

    private fun mostrarNuevoPar() {
        haRespondido = false

        // Restablecer colores a negro
        txtOpcion1.setTextColor(ContextCompat.getColor(this, R.color.negroTexto))
        txtOpcion2.setTextColor(ContextCompat.getColor(this, R.color.negroTexto))

        // Si no hay pares disponibles mostrar resultados finales

        if (pares.isEmpty()) {
            mostrarResultadosFinales()
            return
        }

        // Obtiene la siguiente pareja de palabras
        parActual = pares.removeAt(0)


        // Mezcla la posición de la palabra real y falsa
        if (Math.random() < 0.5) {
            txtOpcion1.text = parActual!!.palabraReal
            txtOpcion2.text = parActual!!.palabraFalsa
        } else {
            txtOpcion1.text = parActual!!.palabraFalsa
            txtOpcion2.text = parActual!!.palabraReal
        }
    }


    //Verifica la respuesta del usuario, y coloreamos verde o rojo segun si acierta o no .

    private fun verificarOpcion(textoPulsado: String, textViewSeleccionado: TextView) {
        if (haRespondido) {
            Toast.makeText(this, "Ya has respondido. Pulsa Siguiente.", Toast.LENGTH_SHORT).show()
            return
        }

        if (parActual == null) {
            Toast.makeText(this, "Error: No hay palabra activa.", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifica si la palabra seleccionada es la correcta
        val acierto = (textoPulsado == parActual!!.palabraReal)
        if (acierto) {
            textViewSeleccionado.setTextColor(ContextCompat.getColor(this, R.color.verdeCorrecto))
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show()
            puntuacion += 10
        } else {
            textViewSeleccionado.setTextColor(ContextCompat.getColor(this, R.color.rojoError))
            Toast.makeText(
                this,
                "Incorrecto, la palabra real era: ${parActual!!.palabraReal}",
                Toast.LENGTH_LONG
            ).show()
            puntuacion = if (puntuacion >= 5) puntuacion - 5 else 0
        }

        haRespondido = true
        actualizarPuntuacionUI()


        // Marca la palabra como usada
        parActual?.let { palabrasDAO.marcarPseudopalabraUsada(it.id) }
    }

    //Marcador de puntos en tiempo real
    private fun actualizarPuntuacionUI() {
        txtPuntuacion.text = "Puntos: $puntuacion"
    }

    //Se asegura de no pasar al siguiente par si no responde
    private fun onClickSiguiente() {
        if (!haRespondido) {
            Toast.makeText(
                this,
                "Responde primero antes de pasar a la siguiente",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        mostrarNuevoPar()
    }

    //Panel final para mostrar el resultado final de la actividad
    private fun mostrarResultadosFinales() {
        // Ocultar elementos de juego
        txtOpcion1.visibility = View.GONE
        txtOpcion2.visibility = View.GONE
        btnSiguiente.visibility = View.GONE

        //Guardamos puntuación en la BBDD
        val resInsertarPuntuaciones =
            puntuacionesDAO.actualizarOInsertarPuntuacion(idUsuario, actividadId, puntuacion)

        if (!resInsertarPuntuaciones) {
            Toast.makeText(this, "Error al guardar la puntuación", Toast.LENGTH_SHORT).show()
        }

        txtPuntuacion.text = "Puntuación final: $puntuacion"

        // Cambiar el texto y funcionalidad del botón "Atras" para que regrese a Home
        btnAtras.text = "Volver a menu principal"
        btnAtras.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
