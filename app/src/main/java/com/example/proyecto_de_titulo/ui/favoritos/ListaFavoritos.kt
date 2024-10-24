package com.example.proyecto_de_titulo.ui.favoritos

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.DataCalculadoras
import com.example.proyecto_de_titulo.data.Datacuestionarios


class ListaFavoritos : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritosAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_favoritos, container, false)
        recyclerView = view.findViewById(R.id.listaCuestionariosFavoritos)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val cuestionariosFavoritos = arguments?.getParcelableArrayList<Datacuestionarios>("cuestionarios")
        val calculadorasFavoritas = arguments?.getParcelableArrayList<DataCalculadoras>("calculadoras")?.filter { it.favorito }

        adapter = if (cuestionariosFavoritos != null) {
            FavoritosAdapter(cuestionariosFavoritos, null)
        } else {
            FavoritosAdapter(null, calculadorasFavoritas ?: emptyList())
        }
        recyclerView.adapter = adapter

        return view
    }
}

class FavoritosAdapter(
    private val cuestionarios: List<Datacuestionarios>?,
    private val calculadoras: List<DataCalculadoras>?
) : RecyclerView.Adapter<FavoritosAdapter.FavoritosViewHolder>() {

    class FavoritosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloCuestionario: Button? = itemView.findViewById(R.id.intentarCuestionario)
        val tituloCalculadoras: TextView? = itemView.findViewById(R.id.TituloCalculadora)
        val botonFavoritoCuestionario: Button? = itemView.findViewById(R.id.guardarFavoritoCuestionario)
        val botonFavoritoCalculadora: Button? = itemView.findViewById(R.id.guardarFavoritoCalculadora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritosViewHolder {
        val view = if (cuestionarios != null) {
            LayoutInflater.from(parent.context).inflate(R.layout.tarjeta_cuestionarios, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.tarjeta_de_calculadoras, parent, false)
        }
        return FavoritosViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritosViewHolder, position: Int) {
        if (cuestionarios != null) {
            val cuestionario = cuestionarios[position]
            holder.tituloCuestionario?.text = cuestionario.id.toString()

            updateFavoriteIcon(holder.botonFavoritoCuestionario, cuestionario.isFavorite)
            holder.botonFavoritoCuestionario?.setOnClickListener {
                cuestionario.isFavorite = !cuestionario.isFavorite
                updateFavoriteIcon(holder.botonFavoritoCuestionario, cuestionario.isFavorite)
                Log.d("FavoritosAdapter", "Item ${cuestionario.titulo} favorito: ${cuestionario.isFavorite}")
            }
        } else if (calculadoras != null) {
            val calculadora = calculadoras[position]
            holder.tituloCalculadoras?.text = calculadora.nombre

            updateFavoriteIcon(holder.botonFavoritoCalculadora, calculadora.favorito)
            holder.botonFavoritoCalculadora?.setOnClickListener {
                calculadora.favorito = !calculadora.favorito
                updateFavoriteIcon(holder.botonFavoritoCalculadora, calculadora.favorito)
                Log.d("FavoritosAdapter", "Item ${calculadora.nombre} favorito: ${calculadora.favorito}")
            }
        }
    }


// After
    private fun updateFavoriteIcon(button: Button?, isFavorite: Boolean) {
        button?.setCompoundDrawablesWithIntrinsicBounds(
            if (isFavorite)
            {
                R.drawable.baseline_favorite_50
            } else {
                R.drawable.baseline_favorite_border_50
            }, 0, 0, 0
        )
    }
    override fun getItemCount(): Int {
        return cuestionarios?.size ?: calculadoras?.size ?: 0
    }
}