package com.example.proyecto_de_titulo.ui.Cuestionarios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.databinding.FragmentCuestionariosBinding
import com.example.proyecto_de_titulo.ui.Cuestionarios.CuestionariosViewModel

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

        // Obtener el array de títulos de los recursos
        val cuestionariosTitulos = resources.getStringArray(R.array.cursos_titulos)
        val cuestionarios = resources.getStringArray(R.array.cuestionarios)

        // Crear el adaptador con la lista de títulos de cuestionarios
        val adapter = CuestionariosAdapter(cuestionariosTitulos, cuestionarios)

        // Configurar el RecyclerView con un LayoutManager y el adaptador
        binding.listadoCuestionarios.layoutManager = LinearLayoutManager(context)
        binding.listadoCuestionarios.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CuestionariosAdapter(
    private val cuestionariosTitulos: Array<String>,
    private val cuestionarios: Array<String>
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
        holder.contenedorTituloCuestionario.text = cuestionariosTitulos[position]

        // Configura el RecyclerView anidado
        val nestedAdapter = NestedCuestionariosAdapter(cuestionarios)
        holder.listadoDeCuestionarios.layoutManager = LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)
        holder.listadoDeCuestionarios.adapter = nestedAdapter
    }

    override fun getItemCount() = cuestionariosTitulos.size
}

class NestedCuestionariosAdapter(
    private val cuestionarios: Array<String>
) : RecyclerView.Adapter<NestedCuestionariosAdapter.NestedViewHolder>() {

    class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cuestionarioTextView: TextView = itemView.findViewById(R.id.numeroCuestionario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_cuestionarios, parent, false)
        return NestedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        holder.cuestionarioTextView.text = cuestionarios[position]
    }

    override fun getItemCount() = cuestionarios.size
}