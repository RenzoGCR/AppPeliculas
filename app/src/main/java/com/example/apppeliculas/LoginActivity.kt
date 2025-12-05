package com.example.apppeliculas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "MI_DATA_STORE")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // Definimos las claves para DataStore una sola vez
    companion object {
        val EMAIL_KEY = stringPreferencesKey("email")
        val PASSWORD_KEY = stringPreferencesKey("password")
        val REMEMBER_KEY = booleanPreferencesKey("recordar")
    }

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

        // 1. Cargar las preferencias guardadas al iniciar
        lifecycleScope.launch(Dispatchers.IO) {
            cargarPreferencias()
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.editText?.text.toString().trim()
            val password = binding.etPassword.editText?.text.toString().trim()
            val recordar = binding.recordar.isChecked

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Iniciar corrutina para guardar datos y navegar
                lifecycleScope.launch(Dispatchers.IO) {
                    guardarPreferencias(email, password, recordar)

                    // Cambiar al hilo principal para navegar a MainActivity
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Cierra LoginActivity para que el usuario no pueda volver
                    }
                }
            } else {
                Toast.makeText(this, "Email y contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private suspend fun guardarPreferencias(email: String, password: String, recordar: Boolean) {
        dataStore.edit { editor ->
            if (recordar) {
                editor[EMAIL_KEY] = email
                editor[PASSWORD_KEY] = password
                editor[REMEMBER_KEY] = true
            } else {
                // Si no se marca "recordar", borramos las credenciales guardadas
                editor.remove(EMAIL_KEY)
                editor.remove(PASSWORD_KEY)
                editor.remove(REMEMBER_KEY)
            }
        }
    }
    private suspend fun cargarPreferencias() {
        val preferences = dataStore.data.first()
        val recordar = preferences[REMEMBER_KEY] ?: false

        if (recordar) {
            val email = preferences[EMAIL_KEY] ?: ""
            val password = preferences[PASSWORD_KEY] ?: ""

            // Para actualizar la UI, volvemos al hilo principal
            withContext(Dispatchers.Main) {
                binding.etEmail.editText?.setText(email)
                binding.etPassword.editText?.setText(password)
                binding.recordar.isChecked = true
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

