package com.example.logodix

import android.content.ContentValues

import android.database.Cursor
import androidx.core.database.sqlite.transaction

class PalabrasDAO(private val dbHelper: DBHelper) {


    // Función para insertar una palabra en la base de datos
    fun insertarPalabra(palabraOriginal: String, nivel: String,idActividad: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("palabra_original", palabraOriginal)
            put("nivel", nivel)
            put("id_actividad", idActividad)
            put("usada", 0)
        }

        val resultado = db.insert("palabras", null, valores)
        db.close()
        return resultado != -1L
    }

    // Función para obtener una palabra aleatoria de un nivel
    fun obtenerPalabraAleatoria(nivel: String, idActividad: Int): String? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT palabra_original FROM palabras WHERE nivel = ? AND id_actividad =? ORDER BY RANDOM() LIMIT 1",
            arrayOf(nivel, idActividad.toString())
        )
        val palabra= if(cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()
        db.close()
        return palabra
    }

    // Método para desordenar una palabra
    fun desordenarPalabra(palabra: String): String {
        val listaLetras = palabra.toCharArray().toList()
        val letrasDesordenadas = listaLetras.shuffled()
        return String(letrasDesordenadas.toCharArray())
    }

    // Método para insertar palabras predefinidas en la base de datos
    fun insertarPalabrasPredefinidas() {
        val db = dbHelper.writableDatabase
        val palabras = listOf(
            Pair("cama", "facil"),
            Pair("dedo", "facil"),
            Pair("tres", "facil"),
            Pair("mesa", "facil"),
            Pair("rojo", "facil"),
            Pair("gafas", "medio"),
            Pair("perro", "medio"),
            Pair("cajón", "medio"),
            Pair("mandar", "medio"),
            Pair("saltar", "medio"),
            Pair("trabajo", "dificil"),
            Pair("segundo", "dificil"),
            Pair("primera", "dificil"),
            Pair("informar", "dificil"),
            Pair("partido", "dificil"),

            )
        db.transaction {

            try {
                for (palabra in palabras) {
                    val valores = ContentValues().apply {
                        put("palabra_original", palabra.first)
                        put("nivel", palabra.second)
                        put("id_actividad", 1)
                        put("usada", 0)
                    }
                    insert("palabras", null, valores)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
            }
            db.close()
        }
    }

    // Método para actualizar una palabra (Actualmente no se usa)
    fun actualizarPalabra(id: Int, nuevaPalabra: String, nuevoNivel: String): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("palabra_original", nuevaPalabra)
            put("nivel", nuevoNivel)
        }
        val resultado = db.update("palabras", valores, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    // Función para eliminar una palabra (Actualmente no se usa)
    fun eliminarPalabra(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("palabras", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }
    //Método para obtener todas las palabras
    fun obtenerTodasPalabras(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT palabra_original FROM palabras ",null)
        val lista= mutableListOf<String>()
        while (cursor.moveToNext()){
            lista.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return lista
    }

    // Método para usar solo las parejas que no se han usado
    fun obtenerPalabrasSinUsar(): List<Pair<Int, String>> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, palabra_original FROM palabras WHERE usada = 0",
            null
        )
        val lista = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()) {
            // Se guarda el id junto con la palabra
            lista.add(Pair(cursor.getInt(0), cursor.getString(1)))
        }
        cursor.close()
        db.close()
        return lista
    }
    //Función para marcar las palabras usadas y que no se repitan en la actividad
    fun marcarPalabraUsada(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("usada", 1)
        }
        val resultado = db.update("palabras", valores, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    //Función para usar palabras usada donde se resetean para que puedan volver a usar la actividad
    fun resetearPalabras() {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("usada", 0)
        }
        db.update("palabras", valores, null, null)
        db.close()
    }



    //Actividad 2: Elige la palabra correcta

    //Función para inserta los pares de palabras
    fun insertarPseudopalabra(palabraReal: String, palabraFalsa: String, nivel: String, idActividad: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("palabra_real", palabraReal)
            put("palabra_falsa", palabraFalsa)
            put("nivel", nivel)
            put("id_actividad", idActividad)
            put("usada",0)
        }
        val resultado = db.insert("pseudopalabras", null, valores)
        db.close()
        return resultado != -1L
    }

    // Obtener una palabra real junto con su pseudopalabra

    data class ParPalabra(val id: Int, val palabraReal: String, val palabraFalsa: String)

    // Método donde se encuentran las pseudopalabras definidas
    fun insertarPseudopalabrasPredefinidas() {
        val db = dbHelper.writableDatabase
        val pseudopalabras = listOf(
            Pair("cama", "cuma"),
            Pair("gato", "guto"),
            Pair("rata", "raca"),
            Pair("mesa", "mefa"),
            Pair("luna", "lura"),
            Pair("perro", "perto"),
            Pair("barco", "bargo"),
            Pair("trabajo", "travajo"),
            Pair("deporte", "deborte"),
            Pair("elefante", "elifando"),
            Pair("grande", "granbe"),
            Pair("almohada", "almoada"),
            Pair("segundo", "sequnbo"),
            Pair("primera", "qrimera"),
            Pair("informar", "infonmar"),
            Pair("partido", "partibo"),
            Pair("revista", "rebista"),
            Pair("mejores", "megores"),
            Pair("entregar", "entrejar"),
            Pair("baloncesto", "valoncesto"),
            Pair("estudiar", "estubiar"),
            Pair("estuche", "estucle"),
        )


        db.transaction {
            try {
                for (par in pseudopalabras) {
                    val valores = ContentValues().apply {
                        put("palabra_real", par.first)
                        put("palabra_falsa", par.second)
                        put("nivel", "facil") // Puedes cambiar los niveles según las palabras
                        put("id_actividad", 2)
                        put("usada", 0)
                    }
                    insert("pseudopalabras", null, valores)
                }
            } finally {
            }
            db.close()
        }
    }
    // Función para actualizar una pseudopalabra (Actualmente no se usa)
    fun actualizarPseudopalabra(id: Int, nuevaPalabraFalsa: String): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("palabra_falsa", nuevaPalabraFalsa)
        }
        val resultado = db.update("pseudopalabras", valores, "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }



    // Función para eliminar una pseudopalabra (Actualmente no se usa)
    fun eliminarPseudopalabra(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("pseudopalabras", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    //Método para marcar los pares de palabras como usada y que no se repitan en la actividad
    fun marcarPseudopalabraUsada(id:Int): Boolean{
        val db = dbHelper.writableDatabase
        val valores= ContentValues().apply {
            put("usada",1)
        }
        val resultado= db.update("pseudopalabras", valores, "id=?", arrayOf(id.toString()))
        db.close()
        return resultado >0
    }

    // Método para obtener todas las parejas, limite 8 parejas
    fun obtenerTodasPseudopalabras(): List<ParPalabra> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, palabra_real, palabra_falsa FROM pseudopalabras WHERE usada=0 limit 8", null)
        val lista = mutableListOf<ParPalabra>()
        while (cursor.moveToNext()) {
            lista.add(
                ParPalabra(
                    id = cursor.getInt(0),
                    palabraReal = cursor.getString(1),
                    palabraFalsa = cursor.getString(2)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }
    //Método para obtener las parejas que esten sin usar
    fun obtenerPseudopalabrasSinUsar(nivel: String): List<ParPalabra> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, palabra_real, palabra_falsa FROM pseudopalabras WHERE nivel = ? AND usada = 0 limit 6",
            arrayOf(nivel)
        )

        val lista = mutableListOf<ParPalabra>()
        while (cursor.moveToNext()) {
            lista.add(
                ParPalabra(
                    id = cursor.getInt(0),
                    palabraReal = cursor.getString(1),
                    palabraFalsa = cursor.getString(2)
                )
            )
        }
        cursor.close()
        db.close()
        return lista
    }

    //Método que resetea las pseudopalabras
    fun resetearPseudopalabras() {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("usada", 0)
        }
        db.update("pseudopalabras", valores, null, null)
        db.close()
    }
}





