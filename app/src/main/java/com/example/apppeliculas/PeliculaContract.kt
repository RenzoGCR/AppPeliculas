package com.example.apppeliculas

import android.provider.BaseColumns

class PeliculaContract {
    companion object{
        const val NOMBRE_BD = "dbpeliculas"
        const val VERSION = 1
        class Entrada:BaseColumns{
            companion object{
                const val TABLA = "peliculas"
                const val IDCOL = "id"
                const val TITULOCOL = "titulo"
                const val DESCRIPCIONCOL = "descripcion"
                const val POSTERCOL = "poster"

                const val DURACIONCOL = "duracion"
                const val ANHOCOL = "anho"
                const val PAISCOL = "pais"


            }
        }
    }
}
