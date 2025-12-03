package com.example.apppeliculas


import android.os.Bundle
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apppeliculas.databinding.ActivityDetalleBinding

class DetalleActivity : AppCompatActivity(){
    private lateinit var binding: ActivityDetalleBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val pelicula = intent.getSerializableExtra("pelicula",Pelicula::class.java)
        binding.tvDetalle.text = pelicula!!.titulo
        binding.ivPeliculaElegida.setImageResource(pelicula!!.poster)
        binding.tvDescripcion.text = "Descripción: ${pelicula.descripcion}"
        binding.tvDuracion.text="Duracion: ${pelicula.duracion}"
        binding.tvAnho.text="Año: ${pelicula.anho}"
        binding.tvNacionalidad.text="Nacionalidad: ${pelicula.pais}"

        binding.btnVolver.setOnClickListener { finish() }

    }
}