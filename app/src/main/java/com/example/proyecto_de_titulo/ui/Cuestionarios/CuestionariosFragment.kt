package com.example.proyecto_de_titulo.ui.Cuestionarios

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.DataSeccionCuestionarios
import com.example.proyecto_de_titulo.data.Datacuestionarios
import com.example.proyecto_de_titulo.databinding.FragmentCuestionariosBinding
import com.example.proyecto_de_titulo.ui.favoritos.ListaFavoritos

class CuestionariosFragment : Fragment() {

    private var _binding: FragmentCuestionariosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cuestionariosViewModel = ViewModelProvider(this).get(CuestionariosViewModel::class.java)

        _binding = FragmentCuestionariosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cuestionariosViewModel.cuestionarios.observe(viewLifecycleOwner) { cuestionarios ->
            val adapter = CuestionariosAdapter(cuestionarios)
            binding.listadoCuestionarios.layoutManager = LinearLayoutManager(context)
            binding.listadoCuestionarios.adapter = adapter
        }

        // Set up the button click listener
        val botonIrFavCuestionario: Button = binding.root.findViewById(R.id.botonIrFavCuestionario)
        botonIrFavCuestionario.setOnClickListener {
            findNavController().navigate(R.id.navigation_favoritosCuestionarios)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CuestionariosAdapter(
    private val cuestionarios: List<DataSeccionCuestionarios>
) : RecyclerView.Adapter<CuestionariosAdapter.CuestionarioViewHolder>() {

    class CuestionarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contenedorTituloCuestionario: TextView = itemView.findViewById(R.id.textView2)
        val listadoDeCuestionarios: RecyclerView = itemView.findViewById(R.id.listadoDeCuestionarios)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuestionarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.titulo_y_cuestionarios, parent, false)
        return CuestionarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuestionarioViewHolder, position: Int) {
        val cuestionario = cuestionarios[position]
        holder.contenedorTituloCuestionario.text = cuestionario.titulo

        // Configura el RecyclerView anidado
        val nestedAdapter = NestedCuestionariosAdapter(cuestionario.lista_cuestionarios)
        holder.listadoDeCuestionarios.layoutManager = LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)
        holder.listadoDeCuestionarios.adapter = nestedAdapter
    }

    override fun getItemCount() = cuestionarios.size
}

// Update NestedCuestionariosAdapter
class NestedCuestionariosAdapter(
    private val preguntas: List<Datacuestionarios>
) : RecyclerView.Adapter<NestedCuestionariosAdapter.NestedViewHolder>() {

    class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preguntaTextView: TextView = itemView.findViewById(R.id.numeroCuestionario)
        val boton: Button = itemView.findViewById(R.id.guardarFavoritoCuestionario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_cuestionarios, parent, false)
        return NestedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        val pregunta = preguntas[position]
        holder.preguntaTextView.text = pregunta.id.toString()

        // Set the button icon based on the favorite state
        if (pregunta.isFavorite) {
            holder.boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_50, 0, 0, 0)
            Log.d("NestedCuestionariosAdapter", "Pregunta ${pregunta.id} isFavorite: true")
        } else {
            holder.boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_border_50, 0, 0, 0)
            Log.d("NestedCuestionariosAdapter", "Pregunta ${pregunta.id} isFavorite: false")
        }

        holder.boton.setOnClickListener {
            // Toggle the favorite state
            pregunta.isFavorite = !pregunta.isFavorite

            // Update the button icon
            if (pregunta.isFavorite) {
                holder.boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_50, 0, 0, 0,)
                Log.d("NestedCuestionariosAdapter", "Pregunta ${pregunta.id} marcada como favorita")
            } else {
                holder.boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_border_50, 0, 0, 0)
                Log.d("NestedCuestionariosAdapter", "Pregunta ${pregunta.id} desmarcada como favorita")
            }
        }
    }

    override fun getItemCount() = preguntas.size
}