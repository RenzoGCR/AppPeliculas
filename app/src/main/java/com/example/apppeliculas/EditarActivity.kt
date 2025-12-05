package com.example.apppeliculas

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apppeliculas.databinding.ActivityDetalleBinding
import com.example.apppeliculas.databinding.ActivityEditarBinding
import com.google.android.material.textfield.TextInputEditText

class EditarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditarBinding
    private var pelicula:Pelicula?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityEditarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        this.pelicula = intent.getSerializableExtra("pelicula",Pelicula::class.java)

        binding.tvPelicula.setText(pelicula?.titulo)

        pelicula?.let{
            binding.ivPeliculaElegida.setImageResource(it.poster)
        }

        binding.btnAceptar.setOnClickListener {
            pelicula?.let { peliculaParaActualizar ->
                val nuevoTitulo = binding.tvPelicula.text.toString().trim()

                if (nuevoTitulo.isNotEmpty()) {
                    // 💡 Lógica CLAVE: Solo se modifica la instancia local de 'Pelicula'
                    // ESTO NO TOCA NINGÚN PeliculaProvider
                    peliculaParaActualizar.titulo = nuevoTitulo

                    // --- INICIO DE LA CORRECCIÓN ---
                    // 2. Llama al DAO para actualizar la base de datos
                    val miDAO = PeliculaDAO()
                    miDAO.actualizarBBDD(this, peliculaParaActualizar)
                    // --- FIN DE LA CORRECCIÓN ---

                    // Preparamos el Intent de resultado
                    val resultadoIntent = Intent()
                    // Devolvemos el objeto Pelicula modificado
                    resultadoIntent.putExtra("pelicula_actualizada", peliculaParaActualizar)
                    setResult(RESULT_OK, resultadoIntent)

                    finish()
                } else {
                    binding.tvPelicula.error = "El título no puede estar vacío"
                }
            }
        }
        binding.btnCancelar.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
