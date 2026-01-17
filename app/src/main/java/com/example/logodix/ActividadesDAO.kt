package com.example.logodix

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class ActividadesDAO(context: Context) {
    private val dbHelper = DBHelper(context)

    // Función para insertar una actividad y el nivel correspondiente
    private fun insertarActividad(nombreActividad: String, nivel: String): Boolean {
        val db=dbHelper.writableDatabase
        val values = ContentValues().apply{
            put("nombre_actividad", nombreActividad)
            put("nivel",nivel)
        }
        return try{
            val result=db.insert("actividades",null, values)
            result>0
        } catch(e:Exception){
            e.printStackTrace()
            false
        } finally {
            db.close()
        }

    }

    // Función para insertar las actividades predefinidas
    fun insertarActividadesPredefinidas() {
        val actividades = listOf(
            Pair("Formar palabras", "facil"),
            Pair("Formar palabras", "medio"),
            Pair("Formar palabras", "dificil"),
            Pair("Elegir la palabra real", "facil"),
            Pair("Elegir la palabra real", "medio"),
            Pair("Elegir la palabra real", "dificil")
        )


        for (actividad in actividades) {
            insertarActividad(actividad.first, actividad.second)
        }
    }

    // Función para obtener todas las actividades
    fun obtenerTodasLasActividades(): List<Pair<Int, String>> {
            val db = dbHelper.readableDatabase
            val query = "SELECT id, nombre_actividad FROM actividades"
            val cursor: Cursor = db.rawQuery(query, null)
            val actividades = mutableListOf<Pair<Int, String>>()

            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                actividades.add(Pair(id, nombre))
            }
            cursor.close()
            db.close()
            return actividades
        }

    //  Función para actualizar el nombre y nivel de una actividad
    fun actualizarActividad(id: Int, nuevoNombre: String, nuevoNivel: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre_actividad", nuevoNombre)
            put("nivel", nuevoNivel)
        }
        val resultado = db.update("actividades", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    //  Función para eliminar una actividad segun su ID
    fun eliminarActividad(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("actividades", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }
}
