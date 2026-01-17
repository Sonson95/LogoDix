package com.example.logodix

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar la base de datos
        dbHelper = DBHelper(this)
        val usuarioDAO= UsuarioDAO(this)

        val txtNombre = findViewById<EditText>(R.id.txtRegistroNombre)
        val txtCorreo = findViewById<EditText>(R.id.txtRegistroCorreo)
        val txtPassword = findViewById<EditText>(R.id.txtRegistroPassword)
        val txtConfirmPassword = findViewById<EditText>(R.id.txtConfirmPassword)
        val txtPregunta = findViewById<EditText>(R.id.txtRegistroPregunta)
        val txtRespuesta = findViewById<EditText>(R.id.txtRegistroRespuesta)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCreaCuenta)

        // Evento para crear cuenta
        btnCrearCuenta.setOnClickListener {
            val nombre = txtNombre.text.toString().trim()
            val correo = txtCorreo.text.toString().trim()
            val password = txtPassword.text.toString().trim()
            val confirmPassword = txtConfirmPassword.text.toString().trim()
            val pregunta = txtPregunta.text.toString().trim()
            val respuesta = txtRespuesta.text.toString().trim()

            // Valida que todos los campos estan completos
            if (correo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {

                // Insertar usuario en la base de datos SQLite
                val success = usuarioDAO.insertarUsuario(nombre, correo, password,pregunta, respuesta)
                if (success) {
                    Toast.makeText(this, "Cuenta creada para: $nombre", Toast.LENGTH_SHORT).show()

                    val idUsuario= usuarioDAO.obtenerIdPorCorreo(correo)?:0

                    //Redirige al login
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("ID_USUARIO", idUsuario)
                    startActivity(intent)
                    finish()  // Cierra la actividad de registro
                } else {
                    Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
