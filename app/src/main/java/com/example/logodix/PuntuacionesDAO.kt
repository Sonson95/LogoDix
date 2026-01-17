package com.example.logodix

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log

class PuntuacionesDAO(context: Context) {
    private val dbHelper= DBHelper(context)

    // Método para insertar puntuaciones
    private fun insertarPuntuacion(idUsuario: Int, idActividad: Int, puntuacion: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_usuario",idUsuario)
            put("id_actividad", idActividad)
            put("puntuacion", puntuacion)
        }
        val result = db.insert("puntuaciones", null,values) >0
        db.close()
        return result
    }

    // Método para actualizar o insertar puntuaciones
    fun actualizarOInsertarPuntuacion(idUsuario: Int, actividadId: Int, puntuacion: Int): Boolean {
        // Consulta si ya existe una puntuación para este usuario y actividad
        val puntuacionExistente = obtenerPuntuacionUsuarioActividad(idUsuario, actividadId)
        Log.d("sonia elegir palabra", idUsuario.toString())

        if (puntuacionExistente == null) {
            val insercion = insertarPuntuacion(idUsuario, actividadId, puntuacion)
            return insercion
        } else {
            return actualizarPuntuacion(idUsuario, actividadId,puntuacion)
        }
    }

    // Función para obtener las puntuaciones de un usuario (Actualmente no se usa)
    fun obtenerPuntuacionesUsuario(idUsuario: Int): List<String> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT a.nombre_actividad, p.puntuacion, a.nivel 
            FROM puntuaciones p 
            JOIN actividades a ON p.id_actividad = a.id 
            WHERE p.id_usuario = ?
        """
        val cursor: Cursor = db.rawQuery(query, arrayOf(idUsuario.toString()))
        val puntuaciones = mutableListOf<String>()

        while (cursor.moveToNext()){
            val actividad = cursor.getString(0)
            val puntuacion= cursor.getInt(1)
            val nivel= cursor.getString(2)
            puntuaciones.add("$actividad ($nivel): $puntuacion puntos")
        }
        cursor.close()
        db.close()

        return puntuaciones
    }

    // Método para actualizar puntuación de un usuario en una actividad
    private fun actualizarPuntuacion(idUsuario: Int, idActividad: Int, nuevaPuntuacion: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("puntuacion", nuevaPuntuacion)
        }
        val resultado = db.update("puntuaciones", valores, "id_usuario = ? AND id_actividad = ?",
            arrayOf(idUsuario.toString(), idActividad.toString()))
        db.close()
        return resultado > 0
    }

    // Método para eliminar puntuaciones de un usuario (Actualmente no se usa)
    fun eliminarPuntuacionesUsuario(idUsuario: Int): Boolean {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("puntuaciones", "id_usuario = ?", arrayOf(idUsuario.toString()))
        db.close()
        return resultado > 0
    }

    data class Puntuacion(
        val usuario: String,
        val puntos: Int,
        val actividad: String
    )


    // Método para obtener las puntuaciones de cada actividad
    private fun obtenerPuntuacionUsuarioActividad(idUsuario: Int, idActividad: Int): Int? {
        val db = dbHelper.readableDatabase
        val query = "SELECT puntuacion FROM puntuaciones WHERE id_usuario = ? AND id_actividad = ?"
        val cursor = db.rawQuery(query, arrayOf(idUsuario.toString(), idActividad.toString()))
        var resultado: Int? = null
        if (cursor.moveToFirst()) {
            resultado = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return resultado
    }

    //Método para obtener las puntuaciones globales
    fun obtenerPuntuacionesGlobales(): List<Puntuacion> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT u.nombre, p.puntuacion, a.nombre_actividad 
            FROM puntuaciones p 
            JOIN usuarios u ON p.id_usuario = u.id 
            JOIN actividades a ON p.id_actividad = a.id 
            ORDER BY p.puntuacion DESC
        """
        val cursor: Cursor = db.rawQuery(query, null)
        val lista = mutableListOf<Puntuacion>()
        while (cursor.moveToNext()){
            val usuario = cursor.getString(0)
            val puntos = cursor.getInt(1)
            val actividad = cursor.getString(2)
            lista.add(Puntuacion(usuario, puntos, actividad))
        }
        cursor.close()
        db.close()
        return lista
    }

}