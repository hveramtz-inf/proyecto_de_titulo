package com.example.proyecto_de_titulo.ui.Cursos

import android.os.Bundle
import android.util.Log
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
import com.example.proyecto_de_titulo.databinding.FragmentCursosBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.proyecto_de_titulo.dataApiRest.CursoApi
import com.example.proyecto_de_titulo.dataApiRest.SeccionApi
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient

class HomeFragment : Fragment() {

    private var _binding: FragmentCursosBinding? = null
    private val binding get() = _binding!!
    private lateinit var cursosAdapter: CursosAdapter
    private lateinit var seccionesAdapter: SeccionesAdapter
    private var previousAdapter: RecyclerView.Adapter<*>? = null
    private var originalCursos: List<CursoApi> = emptyList() // List to store original data

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cursosViewModel = ViewModelProvider(this).get(CursosViewModel::class.java)

        _binding = FragmentCursosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the adapter with an empty list
        cursosAdapter = CursosAdapter(emptyList())

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
            val cursosFiltrados = originalCursos.filter { it.nombre.contains(query, ignoreCase = true) }
            cursosAdapter.updateData(cursosFiltrados)
        }

        showLoadingCursos()

        // Fetch cursos from API
        RetrofitClient.cursoApiService.getCursos().enqueue(object : Callback<List<CursoApi>> {
            override fun onResponse(
                call: Call<List<CursoApi>>,
                response: Response<List<CursoApi>>
            ) {
                hideLoadingCursos()
                if (response.isSuccessful) {
                    val cursos = response.body() ?: emptyList()
                    originalCursos = cursos // Store the original data
                    cursosAdapter.updateData(cursos)
                } else {
                    Log.e("CursosFragment", "Failed to fetch cursos: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<CursoApi>>, t: Throwable) {
                hideLoadingCursos()
                Log.e("CursosFragment", "Error fetching cursos", t)
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoadingCursos() {
        cursosAdapter = CursosAdapter(listOf(null))
        binding.listadoCursos.adapter = cursosAdapter
    }

    private fun hideLoadingCursos() {
        cursosAdapter = CursosAdapter(emptyList())
        binding.listadoCursos.adapter = cursosAdapter
    }

    private fun showLoadingSecciones() {
        seccionesAdapter = SeccionesAdapter(listOf(null))
        binding.listadoCursos.adapter = seccionesAdapter
    }

    private fun hideLoadingSecciones() {
        seccionesAdapter = SeccionesAdapter(emptyList())
        binding.listadoCursos.adapter = seccionesAdapter
    }

    inner class CursosAdapter(private var cursos: List<CursoApi?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val VIEW_TYPE_ITEM = 0
        private val VIEW_TYPE_LOADING = 1

        inner class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cursoTitulo: TextView = itemView.findViewById(R.id.TituloCurso)
        }

        inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_ITEM) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tarjeta_de_cursos, parent, false)
                CursoViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_item, parent, false)
                LoadingViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is CursoViewHolder) {
                val curso = cursos[position]
                holder.cursoTitulo.text = curso?.nombre
                holder.cursoTitulo.setOnClickListener {
                    // Save the current adapter
                    previousAdapter = binding.listadoCursos.adapter

                    // Fetch secciones from API
                    showLoadingSecciones()
                    RetrofitClient.seccionApiService.getSecciones()
                        .enqueue(object : Callback<List<SeccionApi>> {
                            override fun onResponse(
                                call: Call<List<SeccionApi>>,
                                response: Response<List<SeccionApi>>
                            ) {
                                hideLoadingSecciones()
                                if (response.isSuccessful) {
                                    val secciones = response.body() ?: emptyList()
                                    Log.d("CursosFragment", "Secciones fetched: $secciones")
                                    // Filter secciones based on idcurso
                                    val filteredSecciones = secciones.filter { it.idcurso == curso?.id }
                                    // Define the new adapter with the filtered data
                                    val nuevoAdapter = SeccionesAdapter(filteredSecciones)
                                    // Replace the RecyclerView adapter
                                    binding.listadoCursos.adapter = nuevoAdapter
                                    // Make the back button visible
                                    binding.botonRetroceder.visibility = View.VISIBLE
                                } else {
                                    Log.e("CursosFragment", "Failed to fetch secciones: ${response.errorBody()}")
                                }
                            }

                            override fun onFailure(call: Call<List<SeccionApi>>, t: Throwable) {
                                hideLoadingSecciones()
                                Log.e("CursosFragment", "Error fetching secciones", t)
                            }
                        })
                }
            }
        }

        override fun getItemCount() = cursos.size

        override fun getItemViewType(position: Int): Int {
            return if (cursos[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
        }

        fun updateData(newCursos: List<CursoApi?>) {
            this.cursos = newCursos
            notifyDataSetChanged()
        }
    }

    inner class SeccionesAdapter(private val secciones: List<SeccionApi?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val VIEW_TYPE_ITEM = 0
        private val VIEW_TYPE_LOADING = 1

        inner class SeccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val seccionTitulo: TextView = itemView.findViewById(R.id.TituloCurso)
        }

        inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_ITEM) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tarjeta_de_secciones_curso, parent, false)
                SeccionViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_item, parent, false)
                LoadingViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SeccionViewHolder) {
                val seccion = secciones[position]
                holder.seccionTitulo.text = seccion?.titulo
                holder.seccionTitulo.setOnClickListener {
                    val bundle = bundleOf("seccionId" to seccion?.id)
                    findNavController().navigate(R.id.navigation_seccionCurso, bundle)
                }
            }
        }

        override fun getItemCount() = secciones.size

        override fun getItemViewType(position: Int): Int {
            return if (secciones[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
        }
    }
}