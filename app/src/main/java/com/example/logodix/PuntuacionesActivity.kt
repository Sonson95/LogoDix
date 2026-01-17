package com.example.logodix

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PuntuacionesActivity : AppCompatActivity() {

    private lateinit var tablaGlobal: TableLayout
    private lateinit var txtActividadGlobal: TextView
    private lateinit var btnAtras: Button
    private lateinit var puntuaciones: PuntuacionesDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_puntuaciones)

        tablaGlobal = findViewById(R.id.tablaGlobal)
        txtActividadGlobal = findViewById(R.id.txtActividadGlobal)
        btnAtras = findViewById(R.id.btnAtras)

        // Inicializar DAO
        puntuaciones = PuntuacionesDAO(this)

        // Cargar la tabla global de puntuaciones
        refrescarPuntuacionesGlobales()


        btnAtras.setOnClickListener {
            finish()
        }
    }
    //Método que se encarga de actualizar las puntuaciones
    private fun refrescarPuntuacionesGlobales() {
        // Limpiar filas existentes (dejando el encabezado, que está en la fila 0)
        while (tablaGlobal.childCount > 1) {
            tablaGlobal.removeViewAt(1)
        }
        val listaGlobal = puntuaciones.obtenerPuntuacionesGlobales()
        if (listaGlobal.isEmpty()) {
            txtActividadGlobal.text = "Sin puntuaciones globales"
        } else {
            txtActividadGlobal.text = "Puntuaciones globales"
            for (score in listaGlobal) {
                val fila = TableRow(this)

                // Columnas de la tabla
                val vUsuario = TextView(this).apply {
                    text = score.usuario
                    setPadding(8,8,8,8)
                    setTextColor(Color.BLACK)
                }

                val vActividad = TextView(this).apply {
                    text = score.actividad
                    setPadding(8,8,8,8)
                    setTextColor(Color.BLACK)
                }

                val vPuntos = TextView(this).apply {
                    text = score.puntos.toString()
                    setPadding(8,8,8,8)
                    setTextColor(Color.BLACK)
                }

                fila.addView(vUsuario)
                fila.addView(vActividad)
                fila.addView(vPuntos)
                tablaGlobal.addView(fila)
            }
        }
    }
}








