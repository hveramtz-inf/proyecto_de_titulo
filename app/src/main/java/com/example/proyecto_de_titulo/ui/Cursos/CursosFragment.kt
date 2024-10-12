package com.example.proyecto_de_titulo.ui.Cursos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.DataSeccionCursos
import com.example.proyecto_de_titulo.data.dataCursos
import com.example.proyecto_de_titulo.data.listaCursos
import com.example.proyecto_de_titulo.databinding.FragmentCursosBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentCursosBinding? = null
    private val binding get() = _binding!!
    private lateinit var cursosAdapter: CursosAdapter
    private var previousAdapter: RecyclerView.Adapter<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cursosViewModel = ViewModelProvider(this).get(CursosViewModel::class.java)

        _binding = FragmentCursosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Use the listaCursos from dataCursos.kt
        cursosAdapter = CursosAdapter(listaCursos)

        // Configure the RecyclerView with a LayoutManager and the adapter
        binding.listadoCursos.layoutManager = LinearLayoutManager(context)
        binding.listadoCursos.adapter = cursosAdapter

        // Set up the back button
        binding.botonRetroceder.setOnClickListener {
            previousAdapter?.let {
                binding.listadoCursos.adapter = it
                previousAdapter = null
                binding.botonRetroceder.visibility = View.GONE // Hide the back button
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class CursosAdapter(private val cursos: List<dataCursos>) :
        RecyclerView.Adapter<CursosAdapter.CursoViewHolder>() {

        inner class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cursoTitulo: TextView = itemView.findViewById(R.id.TituloCurso)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.tarjeta_de_cursos, parent, false)
            return CursoViewHolder(view)
        }

        override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
            val curso = cursos[position]
            holder.cursoTitulo.text = curso.string
            holder.cursoTitulo.setOnClickListener {
                // Save the current adapter
                previousAdapter = binding.listadoCursos.adapter

                // Define the new adapter with the data you want to display
                val nuevoAdapter = SeccionesAdapter(curso.lista_secciones)

                // Replace the RecyclerView adapter
                binding.listadoCursos.adapter = nuevoAdapter

                // Make the back button visible
                binding.botonRetroceder.visibility = View.VISIBLE
            }
        }

        override fun getItemCount() = cursos.size
    }

    inner class SeccionesAdapter(private val secciones: List<DataSeccionCursos>) :
        RecyclerView.Adapter<SeccionesAdapter.SeccionViewHolder>() {

        inner class SeccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val seccionTitulo: TextView = itemView.findViewById(R.id.TituloCurso)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeccionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.tarjeta_de_cursos, parent, false)
            return SeccionViewHolder(view)
        }

        override fun onBindViewHolder(holder: SeccionViewHolder, position: Int) {
            holder.seccionTitulo.text = secciones[position].titulo
        }

        override fun getItemCount() = secciones.size
    }
}