package com.example.apppeliculas

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apppeliculas.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listaPeliculas: MutableList<Pelicula>
    private lateinit var adapter: PeliculaAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var miDAO: PeliculaDAO
    private var listaVacia=false

    private val editActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if (result.resultCode==RESULT_OK){
                val data:Intent? = result.data
                val peliculaActualizada:Pelicula?=
                    data?.getSerializableExtra("pelicula_actualizada", Pelicula::class.java)
                peliculaActualizada?.let { updatedPelicula->
                    val index = listaPeliculas.indexOfFirst { it.id == updatedPelicula.id }
                    if (index!=-1){
                        listaPeliculas[index]=updatedPelicula
                        adapter.notifyItemChanged(index)
                        display("Titulo actualizado a: ${updatedPelicula.titulo}")
                    }
                }
            }else if (result.resultCode==RESULT_CANCELED){
                display("Edicion cancelada")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        miDAO= PeliculaDAO()
        listaPeliculas= miDAO.cargarLista(this)
        listaVacia=false
        layoutManager= LinearLayoutManager(this)
        binding.rvPelicula.layoutManager=layoutManager
        adapter = PeliculaAdapter(listaPeliculas){pelicula ->
            onItemSelected(pelicula)
        }
        binding.rvPelicula.adapter = adapter
        binding.rvPelicula.setHasFixedSize(true)
        binding.rvPelicula.itemAnimator= DefaultItemAnimator()
        setupSwipeRefresh()
        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                filterList(p0)
                return true
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addLibro ->{
                addPelicula()
                true
            }
            R.id.delete ->{
                limpiar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun recargar(){
        listaPeliculas.clear()
        listaPeliculas.addAll(PeliculaProvider.cargarLista())
        adapter.notifyDataSetChanged()
    }

    private fun limpiar(){
        val tamanho=listaPeliculas.size
        listaPeliculas.clear()
        adapter.notifyItemRangeRemoved(0,tamanho)
        display("Se han eliminado todas las peliculas")

    }

    private fun addPelicula() {
        val peliculaNueva = Pelicula(
            listaPeliculas[listaPeliculas.size-1].id+1,"desconocido"
            ,"Desconocida", R.drawable.ic_launcher_background,0,0,"desconocido"
        )
        listaPeliculas.add(listaPeliculas.size,peliculaNueva)
        //miDAO.insertarBBDD(this, libroNuevo)
        adapter.notifyItemInserted(listaPeliculas.size)
        layoutManager.scrollToPosition(listaPeliculas.size)
    }

    private fun onItemSelected(pelicula: Pelicula){
        var intent= Intent(this, DetalleActivity::class.java)
        intent.putExtra("pelicula",pelicula)
        startActivity(intent)}
    private fun display(message:String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onContextItemSelected(item:MenuItem):Boolean{
        val peliculaAfectada:Pelicula = listaPeliculas[item.groupId]

        when(item.itemId){
            0->{
                val alert=
                    AlertDialog.Builder(this).setTitle("Eliminar ${peliculaAfectada.titulo}")
                        .setMessage(
                                "¿Estas seguro que quieres eliminar ${peliculaAfectada.titulo}?"
                        )
                        .setNeutralButton("Cerrar",null).setPositiveButton(
                            "Aceptar"
                        ) {_,_ ->
                            display("Se ha eliminado ${peliculaAfectada.titulo}")
                            listaPeliculas.removeAt(item.groupId)
                            adapter.notifyItemRemoved(item.groupId)
                        }.create()
                alert.show()
            }
            1->{
                var intent= Intent(this, EditarActivity::class.java)
                intent.putExtra("pelicula",peliculaAfectada)
                editActivityResultLauncher.launch(intent)
            }
            else -> return super.onContextItemSelected(item)
        }
        return true
    }

    private fun setupSwipeRefresh(){
        binding.srlDatos.setOnRefreshListener {        // Limpia el filtro de búsqueda
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()

            // --- INICIO DE LA CORRECCIÓN ---

            // 1. Carga la lista fresca desde LibroProvider, NO desde miDAO.
            val nuevosLibros = PeliculaProvider.cargarLista()

            // 2. LIMPIA la lista principal de la Activity
            listaPeliculas.clear()

            // 3. AÑADE los nuevos libros a la lista principal
            listaPeliculas.addAll(nuevosLibros)

            // 4. Pasa esta lista completa y actualizada al adapter para que la muestre.
            adapter.setFilteredList(listaPeliculas)

            // --- FIN DE LA CORRECCIÓN ---

            // Detén la animación de refresco
            binding.srlDatos.isRefreshing = false
        }
    }

    private fun filterList(p0:String?){
        if (p0 == null) return
        val query = p0.lowercase()

        // 1. Crea una nueva lista para los resultados filtrados.
        val filteredList = mutableListOf<Pelicula>()

        // 2. Si la búsqueda no está vacía, filtra la lista que ya tienes en memoria.
        if (query.isNotEmpty()) {
            listaPeliculas.forEach { libro ->
                if (libro.titulo.lowercase().contains(query)) {
                    filteredList.add(libro)
                }
            }
        } else {
            // 3. Si la búsqueda está vacía, simplemente añade todos los libros de la lista principal.
            filteredList.addAll(listaPeliculas)
        }

        // 4. Informa al usuario si no hay resultados
        if (filteredList.isEmpty() && query.isNotEmpty()) {
            Toast.makeText(this, "No existe ese libro", Toast.LENGTH_SHORT).show()
        }

        // 5. Pasa la lista (filtrada o completa) al adapter.
        adapter.setFilteredList(filteredList)
    }

}