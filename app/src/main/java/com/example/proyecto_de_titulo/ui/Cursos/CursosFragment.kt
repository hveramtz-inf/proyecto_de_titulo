package com.example.proyecto_de_titulo.ui.Cursos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.DataSeccionCursos
import com.example.proyecto_de_titulo.data.dataCursos
import com.example.proyecto_de_titulo.data.listaCursos
import com.example.proyecto_de_titulo.databinding.FragmentCursosBinding
import com.example.proyecto_de_titulo.ui.Seccion.SeccionesFragment

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

        val buscadoCurso = binding.root.findViewById<EditText>(R.id.buscadorCursos)
        val botonBuscar = binding.root.findViewById<Button>(R.id.bucarCursos)

        botonBuscar.setOnClickListener {
            val query = buscadoCurso.text.toString()
            val filteredCursos = listaCursos.filter { it.string.contains(query, ignoreCase = true) }
            cursosAdapter = CursosAdapter(filteredCursos)
            binding.listadoCursos.adapter = cursosAdapter
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
                .inflate(R.layout.tarjeta_de_secciones_curso, parent, false)
            return SeccionViewHolder(view)
        }

        override fun onBindViewHolder(holder: SeccionViewHolder, position: Int) {
            val seccion = secciones[position]
            holder.seccionTitulo.text = seccion.titulo
            holder.seccionTitulo.setOnClickListener {
                val bundle = bundleOf("seccionId" to seccion.id)
                findNavController().navigate(R.id.navigation_seccionCurso, bundle)
            }
        }

        override fun getItemCount() = secciones.size
    }
}