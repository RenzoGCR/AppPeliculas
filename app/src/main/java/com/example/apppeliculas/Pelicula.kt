package com.example.apppeliculas

import java.io.Serializable

class Pelicula(
    val id: Int,
    var titulo: String,
    val descripcion: String,
    val poster: Int,
    val duracion: Int,
    val anho:Int,
    val pais: String
    ):Serializable