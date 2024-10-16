// CalculadoraFragment.kt
package com.example.proyecto_de_titulo.ui.Calculadoras

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.DataCalculadoras
import com.example.proyecto_de_titulo.data.calculadorasList
import com.example.proyecto_de_titulo.databinding.FragmentCalculadorasBinding
import com.example.proyecto_de_titulo.ui.favoritos.ListaFavoritos

class CalculadoraFragment : Fragment() {

    private var _binding: FragmentCalculadorasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val calculadoraViewModel =
            ViewModelProvider(this).get(CalculadoraViewModel::class.java)

        _binding = FragmentCalculadorasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val botonListaFavoritosCalculadora: Button = binding.root.findViewById(R.id.botonIrFavCalculadora)
        botonListaFavoritosCalculadora.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList("calculadoras", ArrayList(calculadorasList))

            findNavController().navigate(R.id.navigation_favoritosCuestionarios, bundle)
        }
                val listadoCalculadora = root.findViewById<RecyclerView>(R.id.listadoCalculadoras)
        listadoCalculadora.layoutManager = LinearLayoutManager(context)

        listadoCalculadora.adapter = CalculadoraAdapter(calculadorasList)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CalculadoraAdapter(private val items: List<DataCalculadoras>) : RecyclerView.Adapter<CalculadoraAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.TituloCalculadora)
        val formulaTextView: TextView = view.findViewById(R.id.formula)
        val botonFavorito: Button = view.findViewById(R.id.guardarFavoritoCalculadora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_de_calculadoras, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.nombre
        holder.formulaTextView.text = item.formula
        updateFavoriteIcon(holder.botonFavorito, item.favorito)

        holder.botonFavorito.setOnClickListener {
            item.favorito = !item.favorito
            updateFavoriteIcon(holder.botonFavorito, item.favorito)
            Log.d("CalculadoraAdapter", "Item ${item.nombre} favorito: ${item.favorito}")
        }
    }

    private fun updateFavoriteIcon(button: Button, isFavorite: Boolean) {
        if (isFavorite) {
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_50, 0, 0, 0)
        } else {
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_border_50, 0, 0, 0)
        }
    }

    override fun getItemCount(): Int = items.size
}