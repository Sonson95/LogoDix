package com.example.logodix

import android.content.ContentValues
import android.content.Context
import android.database.Cursor


class UsuarioDAO(context: Context) {
    private val dbHelper = DBHelper(context)

    // Método para insertar un nuevo usuario
    fun insertarUsuario(nombre: String, correo: String, password: String, pregunta:String, respuesta:String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply{
            put("nombre", nombre)
            put("correo", correo)
            put("password",password)
            put("pregunta", pregunta)
            put("respuesta", respuesta)
        }
        return db.use { database ->
            database.insert("usuarios", null, values)>0
        }
    }

    // Método para verificar si el correo y la contraseña existen
    fun verificarUsuario(correo: String, password: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM usuarios WHERE correo = ? AND password = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(correo, password))

        val exists = cursor.count > 0 // Si hay registros, el usuario existe
        cursor.close()
        db.close()
        return exists
    }

    // Método para obtener el nombre de un usuario por correo
    fun obtenerNombrePorCorreo(correo: String): String? {
        val db = dbHelper.readableDatabase
        val query = "SELECT nombre FROM usuarios WHERE correo = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(correo))

        val nombre = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        db.close()

        return nombre
    }

    // Función para actualizar el nombre o la contraseña de un usuario (Actualmente no se usa)
    fun actualizarUsuario(correo: String, nuevoNombre: String, nuevaPassword: String): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nuevoNombre)
            put("password", nuevaPassword)
        }
        val resultado = db.update("usuarios", valores, "correo = ?", arrayOf(correo))
        db.close()
        return resultado > 0
    }

    // Función para eliminar usuario (Actualmente no se usa)
    fun eliminarUsuario(correo: String): Boolean {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("usuarios", "correo = ?", arrayOf(correo))
        db.close()
        return resultado > 0
    }
    //Método para obtener el id por correo y se enlace con el resto de tablas
    fun obtenerIdPorCorreo(correo: String): Int? {
        val db = dbHelper.readableDatabase
        val query = "SELECT id FROM usuarios WHERE correo = ?"
        val cursor = db.rawQuery(query, arrayOf(correo))
        val id = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()
        db.close()
        return id
    }

}