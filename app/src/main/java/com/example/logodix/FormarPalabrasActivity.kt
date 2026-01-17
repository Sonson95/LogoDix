package com.example.logodix

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FormarPalabrasActivity : AppCompatActivity() {

    private lateinit var palabrasDAO: PalabrasDAO
    private lateinit var puntuacionesDAO: PuntuacionesDAO

    private lateinit var txtInstrucciones: TextView
    private lateinit var letterContainer: GridLayout
    private lateinit var txtPalabraFormada: TextView
    private lateinit var btnValidar: Button
    private lateinit var btnBorrar: Button
    private lateinit var btnAtras: Button
    private lateinit var txtPuntuacion: TextView
    private val actividadId= 1

    // Palabra correcta obtenida de la BD
    private var palabraCorrecta: String? = null
    private var palabras: MutableList<Pair<Int, String>> = mutableListOf()
    private var palabraCorrectaId: Int? = null

    private var palabraFormada = ""


    private val botonesLetras = mutableListOf<Button>()

    // Variables de progresión de niveles (para mejora en un futuro)
    private val niveles = listOf("facil", "medio", "dificil")
    private var currentLevelIndex = 0

    private var puntuacion = 0
    private var idUsuario=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formar_palabras)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //se obtiene el id de usuario necesario para realizar la actividad
        idUsuario=intent.getIntExtra("ID_USUARIO",0)

        // Inicializamos DBHelper, PalabrasDAO y PuntuacionesDAO
        val dbHelper = DBHelper(this)
        palabrasDAO = PalabrasDAO(dbHelper)
        puntuacionesDAO = PuntuacionesDAO(this)

        palabrasDAO.resetearPalabras()

        // Inserción única de palabras predefinidas
        val listaPalabras = palabrasDAO.obtenerTodasPalabras()
        if (listaPalabras.isEmpty()) {
            palabrasDAO.insertarPalabrasPredefinidas()
        }

        // Referencias a los componentes
        txtInstrucciones = findViewById(R.id.txtInstrucciones)
        letterContainer = findViewById(R.id.contenedorLetras)
        txtPalabraFormada = findViewById(R.id.txtPalabraFormada)
        txtPuntuacion = findViewById(R.id.txtPuntuacion)
        btnValidar = findViewById(R.id.btnValidar)
        btnBorrar = findViewById(R.id.btnBorrar)
        btnAtras = findViewById(R.id.btnAtras)

        btnAtras.setOnClickListener { finish() }
        btnValidar.setOnClickListener { validarPalabra() }
        btnBorrar.setOnClickListener { resetearFormacion() }

        cargarPalabras()
    }
    //Función para actualizar la puntuación mientras se juega
    private fun actualizarPuntuacionUI() {
        txtPuntuacion.text = "Puntuación: $puntuacion"
    }


    //  Método para cargar todas las palabras disponibles  desde la BD, se barajea y muestra la primera palabra.

    private fun cargarPalabras() {
        palabras = palabrasDAO.obtenerPalabrasSinUsar().toMutableList()
        if (palabras.isEmpty()) {

            // Vuelve a cargar las palabras sin usar
            palabras = palabrasDAO.obtenerPalabrasSinUsar().toMutableList()
        }
        palabras.shuffle() // Barajar para orden aleatorio

        //Si se acaban las palabras se muestran los resultados si no se carga una nueva palabra
        if (palabras.isEmpty()) {
            Toast.makeText(this, "No hay palabras disponibles", Toast.LENGTH_SHORT).show()
            mostrarResultadosFinales()
        } else {
            cargarNuevaPalabra()
        }
    }


    //Función para cargar la siguiente palabra de la lista de palabras disponibles. Si la lista está vacía, termina la actividad

    private fun cargarNuevaPalabra() {
        if (palabras.isEmpty()) {
            Toast.makeText(this, " Se han agotado las palabras", Toast.LENGTH_SHORT).show()
            mostrarResultadosFinales()
            return
        }
        // Reiniciamos la palabra formada y el contenedor de letras
        palabraFormada = ""
        txtPalabraFormada.text = "Palabra formada: "
        letterContainer.removeAllViews()
        botonesLetras.clear()

        // Tomamos la siguiente palabra de la lista y la eliminamos de la lista
        val (id, palabra)=palabras.removeAt(0)
        palabraCorrectaId=id
        palabraCorrecta=palabra

        // Desordenar la palabra y crear botones para cada letra
        val palabraDesordenada = palabrasDAO.desordenarPalabra(palabraCorrecta!!)
        val letras = palabraDesordenada.toCharArray().toList()
        for (letra in letras) {
            val btnLetra = Button(this)
            btnLetra.text = letra.toString()

            // Distribución de los botones con las letras
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(4, 4, 4, 4)
            }
            btnLetra.layoutParams = params
            btnLetra.setOnClickListener {
                palabraFormada += letra
                txtPalabraFormada.text = "Palabra formada: $palabraFormada"
                btnLetra.isEnabled = false
            }
            letterContainer.addView(btnLetra)
            botonesLetras.add(btnLetra)
        }
    }

    //Función para validar la palabra formada por el usuario y actualizar puntuación

    private fun validarPalabra() {
        if (palabraCorrecta == null) return

        if (palabraFormada.equals(palabraCorrecta, ignoreCase = true)) {
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show()
            puntuacion += 10
        } else {
            Toast.makeText(
                this,
                "Incorrecto. La palabra correcta era: $palabraCorrecta",
                Toast.LENGTH_LONG
            ).show()
            puntuacion = if (puntuacion >= 5) puntuacion - 5 else 0
        }
        actualizarPuntuacionUI()
        //marca la palabra como usada en la BD
        palabraCorrectaId?.let{
            palabrasDAO.marcarPalabraUsada(it)
        }

        cargarNuevaPalabra()
    }

    //Función que resetea la formación actual para permitir al usuario empezar de nuevo la palabra

    private fun resetearFormacion() {
        palabraFormada = ""
        txtPalabraFormada.text = "Palabra formada: "
        for (btn in botonesLetras) {
            btn.isEnabled = true
        }
    }

    // Panel con la puntuación final
    private fun mostrarResultadosFinales() {

        // Oculta la interfaz de juego
        letterContainer.visibility = View.GONE
        txtInstrucciones.visibility = View.GONE
        txtPalabraFormada.visibility = View.GONE
        btnValidar.visibility = View.GONE
        btnBorrar.visibility = View.GONE

        val resInsertarPuntuaciones = puntuacionesDAO.actualizarOInsertarPuntuacion(idUsuario,actividadId,puntuacion)

        if (!resInsertarPuntuaciones) {
            Toast.makeText(this, "Error al guardar la puntuación", Toast.LENGTH_SHORT).show()
        }

        val panelResultados = findViewById<LinearLayout>(R.id.panelResultados)
        panelResultados.visibility = View.VISIBLE

        val puntosFinales = findViewById<TextView>(R.id.txtPuntosFinales)
        puntosFinales.text = "Puntuación final: $puntuacion"

        btnAtras.text="Volver a menu principal"
        btnAtras.setOnClickListener{
            //Regirigimos a home
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}