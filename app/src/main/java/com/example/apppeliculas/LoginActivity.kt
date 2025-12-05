package com.example.apppeliculas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.apppeliculas.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "MI_DATA_STORE")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var nombre = binding.etNombre.text.toString()
        var password = binding.etPassword.text.toString()
        var terminos = binding.cbTerminos.isChecked
        var edad = binding.etEdad.text.toString()
 /*       lifecycleScope.launch(Dispatchers.IO) {
            guardarDataStore("Pepe", "1234", true, "22")
        }*/
        lifecycleScope.launch(Dispatchers.IO) {
            leerDataStore().collect { response ->
                //Si la respuesta no es nula, mostraremos el texto
              //  if (response != null) {
                    //Para cambiar la UI debemos accedar al hilo principal
                    withContext(Dispatchers.Main) {

                        binding.tvTexto.text = response
                    }
               // }
            }
        }
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                nombre = binding.etNombre.text.toString()
                password = binding.etPassword.text.toString()
                terminos = binding.cbTerminos.isChecked
                edad = binding.etEdad.text.toString()
                guardarDataStore(nombre, password, terminos, edad)

                withContext(Dispatchers.Main) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra LoginActivity para que el usuario no pueda volver con el botón "atrás"
                }
            }
        }

    }

    private suspend fun guardarDataStore(
        nombre: String,
        password: String,
        terminos: Boolean,
        edad: String
    ) {
        val context = this
        context.dataStore.edit { editor ->
            editor[stringPreferencesKey("nombre")] = nombre
            editor[stringPreferencesKey("password")] = password
            editor[booleanPreferencesKey("terminos")] = terminos
            editor[stringPreferencesKey("edad")] = edad
        }
    }

    private fun leerDataStore(): Flow<String> {
        val context: Context = this
        return context.dataStore.data.map { editor ->
            val nombre = editor[stringPreferencesKey("nombre")]
            val password = editor[stringPreferencesKey("password")]
            val terminos = editor[booleanPreferencesKey("terminos")]
            val edad = editor[stringPreferencesKey("edad")]

            if (nombre!=null && password!=null && edad!=null)
                "Bienvenido $nombre, \nPassword: $password,\nTiene $edad años.\nTerminos = ${terminos}"
            else
                "No hay datos guardados"
        }
    }

    private fun borrarDataStore(){
        val context: Context = this
        lifecycleScope.launch(Dispatchers.IO){
            context.dataStore.edit { editor ->
                editor.remove(stringPreferencesKey("edad"))
                editor.remove(booleanPreferencesKey("terminos"))

                //Para todos los datos
                //editor.clear()
            }
        }
    }
}

