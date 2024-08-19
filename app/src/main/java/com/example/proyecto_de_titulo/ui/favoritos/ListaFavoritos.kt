package com.example.proyecto_de_titulo.ui.favoritos

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.Datacuestionarios
import com.example.proyecto_de_titulo.data.cuestionario1
import com.example.proyecto_de_titulo.data.cuestionario2
import com.example.proyecto_de_titulo.data.cuestionario3


class ListaFavoritos : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritosAdapter
    private lateinit var favoritosList: List<Datacuestionarios>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_favoritos, container, false)
        recyclerView = view.findViewById(R.id.listaCuestionariosFavoritos)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Filtrar los cuestionarios favoritos
        favoritosList = listOf(cuestionario1, cuestionario2, cuestionario3).filter { it.isFavorite }

        // Configurar el adaptador
        adapter = FavoritosAdapter(favoritosList)
        recyclerView.adapter = adapter

        return view
    }
}

class FavoritosAdapter(private val cuestionarios: List<Datacuestionarios>) :
    RecyclerView.Adapter<FavoritosAdapter.FavoritosViewHolder>() {

    class FavoritosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.numeroCuestionario)
        val botonFavorito: Button = itemView.findViewById(R.id.guardarFavoritoCuestionario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritosViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_cuestionarios, parent, false)
        return FavoritosViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritosViewHolder, position: Int) {
        val cuestionario = cuestionarios[position]
        holder.titulo.text = cuestionario.titulo

        // Set the button icon based on the favorite state
        if (cuestionario.isFavorite) {
            holder.botonFavorito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_50, 0, 0, 0)
        } else {
            holder.botonFavorito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_border_50, 0, 0, 0)
        }

        holder.botonFavorito.setOnClickListener {
            // Toggle the favorite state
            cuestionario.isFavorite = !cuestionario.isFavorite

            // Update the button icon
            if (cuestionario.isFavorite) {
                holder.botonFavorito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_50, 0, 0, 0)
            } else {
                holder.botonFavorito.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_border_50, 0, 0, 0)
            }
        }
    }

    override fun getItemCount(): Int = cuestionarios.size
}