package com.example.apppeliculas

import android.view.ContextMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.apppeliculas.databinding.ItemPeliculaBinding

class PeliculaViewHolder (view: View): RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
    private val binding = ItemPeliculaBinding.bind(view)
    private lateinit var pelicula: Pelicula
    fun render(item:Pelicula, onClickListener:(Pelicula)-> Unit){
        pelicula=item
        binding.tvPeliculaTitulo.text=item.titulo
        binding.ivPoster.setImageResource(item.poster)

        itemView.setOnClickListener {
            onClickListener(item)
        }
        itemView.setOnCreateContextMenuListener(this)
    }
    override fun onCreateContextMenu(
        p0: ContextMenu?,
        p1:View?,
        p2:ContextMenu.ContextMenuInfo?
    ){
        p0!!.setHeaderTitle(pelicula.titulo)
        p0.add(this.adapterPosition, 0,0,"Eliminar ")
        p0.add(this.adapterPosition,1,1,"Editar")
    }
}