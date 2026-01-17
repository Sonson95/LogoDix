package com.example.logodix

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RecuperarPassword : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        val txtCorreoRecuperar = findViewById<EditText>(R.id.txtCorreoRecuperar)
        val btnObtenerPregunta = findViewById<Button>(R.id.btnObtenerPregunta)
        val lblPreguntaSeguridad = findViewById<TextView>(R.id.lblPreguntaSeguridad)
        val txtRespuestaSeguridad = findViewById<EditText>(R.id.txtRespuestaSeguridad)
        val txtNuevaContrasena = findViewById<EditText>(R.id.txtNuevaContrasena)
        val txtConfirmarContrasena = findViewById<EditText>(R.id.txtConfirmarContrasena)
        val btnActualizarContrasena = findViewById<Button>(R.id.btnActualizarContrasena)

        // Evento para obtener la pregunta de seguridad
        btnObtenerPregunta.setOnClickListener {
            val correo = txtCorreoRecuperar.text.toString().trim()
            if (correo.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce tu correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val pregunta = obtenerPregunta(correo)
            if (pregunta != null) {
                lblPreguntaSeguridad.text = pregunta
                lblPreguntaSeguridad.visibility = View.VISIBLE
                txtRespuestaSeguridad.visibility = View.VISIBLE
                txtNuevaContrasena.visibility = View.VISIBLE
                txtConfirmarContrasena.visibility = View.VISIBLE
                btnActualizarContrasena.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Correo no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento para actualizar la contraseña
        btnActualizarContrasena.setOnClickListener {
            val correo = txtCorreoRecuperar.text.toString().trim()
            val respuesta = txtRespuestaSeguridad.text.toString().trim()
            val nuevaContrasena = txtNuevaContrasena.text.toString().trim()
            val confirmarContrasena = txtConfirmarContrasena.text.toString().trim()

            if (correo.isEmpty() || respuesta.isEmpty() || nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nuevaContrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (validarRespuesta(correo, respuesta)) {
                if (actualizarContrasena(correo, nuevaContrasena)) {
                    Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para obtener la pregunta de seguridad asociada al correo
    private fun obtenerPregunta(correo: String): String? {
        val db = dbHelper.readableDatabase
        val query = "SELECT pregunta FROM usuarios WHERE correo = ?"
        val cursor = db.rawQuery(query, arrayOf(correo))
        var pregunta: String? = null
        if (cursor.moveToFirst()) {
            val preguntaIndex = cursor.getColumnIndex("pregunta")
            if (preguntaIndex >= 0) {  // Asegurarse de que la columna existe
                pregunta = cursor.getString(preguntaIndex)
            }
        }
        cursor.close()
        return pregunta
    }

    // Método para validar la respuesta de seguridad
    private fun validarRespuesta(correo: String, respuesta: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT respuesta FROM usuarios WHERE correo = ?"
        val cursor = db.rawQuery(query, arrayOf(correo))
        var isValid = false
        if (cursor.moveToFirst()) {
            val respuestaIndex = cursor.getColumnIndex("respuesta")
            if (respuestaIndex >= 0) {  // Verificar que la columna existe
                val storedAnswer = cursor.getString(respuestaIndex)
                isValid = storedAnswer.equals(respuesta, ignoreCase = true)
            }
        }
        cursor.close()
        return isValid
    }


    // Método para actualizar la contraseña en la base de datos
    private fun actualizarContrasena(correo: String, nuevaContrasena: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("password", nuevaContrasena)
        }
        val rowsAffected = db.update("usuarios", values, "correo = ?", arrayOf(correo))
        return rowsAffected > 0
    }
}
