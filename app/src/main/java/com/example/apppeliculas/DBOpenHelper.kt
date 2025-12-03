package com.example.apppeliculas

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.apppeliculas.PeliculaProvider
import java.lang.Exception

class DBOpenHelper private constructor(context: Context?) :
    SQLiteOpenHelper(context, PeliculaContract.NOMBRE_BD, null, PeliculaContract.VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(
                "CREATE TABLE ${PeliculaContract.Companion.Entrada.TABLA}"
                        +"(${PeliculaContract.Companion.Entrada.IDCOL} integer primary key"
                        + ",${PeliculaContract.Companion.Entrada.TITULOCOL} NVARCHAR(40) NOT NULL"
                        + ",${PeliculaContract.Companion.Entrada.DESCRIPCIONCOL} NVARCHAR(40) NOT NULL"
                        + ",${PeliculaContract.Companion.Entrada.POSTERCOL} int NOT NULL"
                        + ",${PeliculaContract.Companion.Entrada.DURACIONCOL} int NOT NULL"
                        + ",${PeliculaContract.Companion.Entrada.ANHOCOL} int NOT NULL"
                        + ",${PeliculaContract.Companion.Entrada.PAISCOL} NVARCHAR(40) NOT NULL);"
            )
            // Insertar datos en la tabla
            inicializarBBDD(sqLiteDatabase)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ${PeliculaContract.Companion.Entrada.TABLA};")
        onCreate(sqLiteDatabase)
    }

    private fun inicializarBBDD(db: SQLiteDatabase) {
        val lista = PeliculaProvider.cargarLista()
        for (pelicula in lista) {
            db.execSQL(
                ("INSERT INTO ${PeliculaContract.Companion.Entrada.TABLA}("+
                        "${PeliculaContract.Companion.Entrada.TITULOCOL},"+
                        "${PeliculaContract.Companion.Entrada.DESCRIPCIONCOL},"+
                        "${PeliculaContract.Companion.Entrada.POSTERCOL},"+
                        "${PeliculaContract.Companion.Entrada.DURACIONCOL},"+
                        "${PeliculaContract.Companion.Entrada.ANHOCOL},"+
                        "${PeliculaContract.Companion.Entrada.PAISCOL})"+
                        " VALUES (${pelicula.titulo},'${pelicula.descripcion}','${pelicula.poster}',${pelicula.duracion},'${pelicula.anho}','${pelicula.pais}');")
            )
        }
    }


    companion object {
        private var dbOpen: DBOpenHelper? = null
        fun getInstance(context: Context?): DBOpenHelper? {
            if (dbOpen == null) dbOpen = DBOpenHelper(context)
            return dbOpen
        }
    }
}

