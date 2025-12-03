package com.example.apppeliculas

import android.content.ContentValues
import android.content.Context
import android.database.Cursor


class PeliculaDAO {
    fun cargarLista(context: Context?): MutableList<Pelicula> {
        lateinit var res:MutableList<Pelicula>
        lateinit var c: Cursor
        try {
            // Obtener acceso de solo lectura
            val db = DBOpenHelper.getInstance(context)!!.readableDatabase
            // val sql = "SELECT * FROM frutas;"
            // c = db.rawQuery(sql, null)
            val columnas = arrayOf(
                PeliculaContract.Companion.Entrada.IDCOL,
                PeliculaContract.Companion.Entrada.TITULOCOL,
                PeliculaContract.Companion.Entrada.DESCRIPCIONCOL,
                PeliculaContract.Companion.Entrada.POSTERCOL,
                PeliculaContract.Companion.Entrada.DURACIONCOL,
                PeliculaContract.Companion.Entrada.ANHOCOL,
                PeliculaContract.Companion.Entrada.PAISCOL
            )
            c = db.query(PeliculaContract.Companion.Entrada.TABLA,
                columnas,null,null,null,null,null)
            res= mutableListOf()
            // Leer resultados del cursor e insertarlos en la lista
            while (c.moveToNext()) {
                val nueva = Pelicula(c.getInt(0),c.getString(1),
                    c.getString(2),c.getInt(3),c.getInt(4),
                    c.getInt(5),c.getString(6))
                res.add(nueva)
            }
        } finally {
            c.close()
        }
        return res
    }

    fun actualizarBBDD(context: Context?, pelicula: Pelicula) {
        val db = DBOpenHelper.getInstance(context)!!.writableDatabase
        /*db.execSQL(
            "UPDATE frutas "
                    + "SET nombre='${fruta.nombre}' " +
                    "SET descripcion='${fruta.descripcion}'" +
                    "SET imagen='${fruta.imagen}'" +
                    "WHERE id=${fruta.id};"
        )
        */
        val values = ContentValues()
        values.put(PeliculaContract.Companion.Entrada.IDCOL,pelicula.id)
        values.put(PeliculaContract.Companion.Entrada.TITULOCOL,pelicula.titulo)
        values.put(PeliculaContract.Companion.Entrada.DESCRIPCIONCOL,pelicula.descripcion)
        values.put(PeliculaContract.Companion.Entrada.POSTERCOL,pelicula.poster)
        values.put(PeliculaContract.Companion.Entrada.DURACIONCOL,pelicula.duracion)
        values.put(PeliculaContract.Companion.Entrada.ANHOCOL,pelicula.anho)
        values.put(PeliculaContract.Companion.Entrada.PAISCOL,pelicula.pais)
        db.update(PeliculaContract.Companion.Entrada.TABLA,values,"id=?", arrayOf(pelicula.id.toString()))
        db.close()
    }

    fun insertarBBDD(context: Context?, pelicula:Pelicula){
        val db = DBOpenHelper.getInstance(context)!!.writableDatabase
        db.execSQL(
            "INSERT INTO peliculas (titulo, descripcion, poster, duracion, anho, pais) VALUES "
                    + "(${pelicula.titulo}, "
                    + "('${pelicula.descripcion}', "
                    + "('${pelicula.poster}', "
                    + "(${pelicula.duracion}, "
                    + "('${pelicula.anho}', "
                    + "${pelicula.pais});"
        )
        /*
        val values = ContentValues()
        values.put(FrutaContract.Companion.Entrada.COLUMNA_ID,fruta.id)
        values.put(FrutaContract.Companion.Entrada.COLUMNA_NOMBRE,fruta.nombre)
        values.put(FrutaContract.Companion.Entrada.COLUMNA_DESCRIPCION,fruta.descripcion)
        values.put(FrutaContract.Companion.Entrada.COLUMNA_IMAGEN,fruta.imagen)
        db.insert(FrutaContract.Companion.Entrada.NOMBRE_TABLA,null,values)*/
        db.close()
    }

    fun eliminar(context: Context?, pelicula: Pelicula){
        val db = DBOpenHelper.getInstance(context)!!.writableDatabase
        val sql = "DELETE FROM peliculas WHERE id=${pelicula.id};"
        db.execSQL(
            sql
        )

        /* val values = arrayOf((fruta.id).toString())
         db.delete(FrutaContract.Companion.Entrada.NOMBRE_TABLA,"id=?",values)*/
        db.close()
    }
    fun borrarPeliculas(context:Context?){
        val db = DBOpenHelper.getInstance(context)!!.writableDatabase
        db.execSQL(
            "DELETE FROM peliculas;"
        )
        db.close()
    }
    fun cargarListaOriginal(context:Context?):MutableList<Pelicula>{
        borrarPeliculas(context)
        for(pelicula:Pelicula in PeliculaProvider.cargarLista()){
            insertarBBDD(context,pelicula)
        }
        return cargarLista(context)
    }
}