package com.example.proyecto_de_titulo.ui.Cuestionarios

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.dataApiRest.CuestionarioApi
import com.example.proyecto_de_titulo.dataApiRest.CursoApi
import com.example.proyecto_de_titulo.databinding.FragmentCuestionariosBinding
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CuestionariosFragment : Fragment() {

    private var _binding: FragmentCuestionariosBinding? = null
    private val binding get() = _binding!!
    private lateinit var cuestionariosAdapter: CuestionariosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuestionariosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the adapter with an empty list
        cuestionariosAdapter = CuestionariosAdapter(emptyList())

        // Configure the RecyclerView with a LayoutManager and the adapter
        binding.listadoCuestionarios.layoutManager = LinearLayoutManager(context)
        binding.listadoCuestionarios.adapter = cuestionariosAdapter

        // Show the loading item before making the API call
        showLoading()

        // Fetch cursos from API
        obtenerCursos()

        return root
    }

    private fun obtenerCursos() {
        val apiService = RetrofitClient.cursoApiService
        apiService.getCursos().enqueue(object : Callback<List<CursoApi>> {
            override fun onResponse(call: Call<List<CursoApi>>, response: Response<List<CursoApi>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val cursos = response.body() ?: emptyList()
                    cuestionariosAdapter.updateData(cursos)
                } else {
                    Toast.makeText(context, "Failed to load courses", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CursoApi>>, t: Throwable) {
                hideLoading()
                Toast.makeText(context, "Error loading courses", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading() {
        cuestionariosAdapter.updateData(listOf(null))
    }

    private fun hideLoading() {
        cuestionariosAdapter.updateData(emptyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CuestionariosAdapter(
    private var cursos: List<CursoApi?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    inner class CuestionarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contenedorTituloCuestionario: TextView = itemView.findViewById(R.id.textView2)
        val listadoDeCuestionarios: RecyclerView = itemView.findViewById(R.id.listadoDeCuestionarios)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.titulo_y_cuestionarios, parent, false)
            CuestionarioViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.loading_item, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CuestionarioViewHolder) {
            val curso = cursos[position]
            holder.contenedorTituloCuestionario.text = curso?.nombre

            // Llamada a la API para obtener los cuestionarios por curso
            val apiService = RetrofitClient.cuestionarioApiService
            apiService.getCuestionarios(curso?.id.toString()).enqueue(object : Callback<List<CuestionarioApi>> {
                override fun onResponse(call: Call<List<CuestionarioApi>>, response: Response<List<CuestionarioApi>>) {
                    if (response.isSuccessful) {
                        val cuestionarios = response.body()
                        if (cuestionarios != null) {
                            val nestedAdapter = NestedCuestionariosAdapter(cuestionarios)
                            holder.listadoDeCuestionarios.layoutManager = LinearLayoutManager(holder.itemView.context, RecyclerView.HORIZONTAL, false)
                            holder.listadoDeCuestionarios.adapter = nestedAdapter
                        }
                    } else {
                        Toast.makeText(holder.itemView.context, "Failed to load cuestionarios", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CuestionarioApi>>, t: Throwable) {
                    Toast.makeText(holder.itemView.context, "Error loading cuestionarios", Toast.LENGTH_SHORT).show()
                }
            })
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

class NestedCuestionariosAdapter(
    private val cuestionarios: List<CuestionarioApi>
) : RecyclerView.Adapter<NestedCuestionariosAdapter.NestedViewHolder>() {

    class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preguntaTextView: Button = itemView.findViewById(R.id.intentarCuestionario)
        val boton: Button = itemView.findViewById(R.id.guardarFavoritoCuestionario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_cuestionarios, parent, false)
        return NestedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        val cuestionario = cuestionarios[position]
        holder.preguntaTextView.text = (position + 1).toString()

        holder.preguntaTextView.setOnClickListener {
            val intent: Intent = Intent(holder.itemView.context, IntentarCuestionario::class.java)
            intent.putExtra("idCuestionario", cuestionario.id.toString())
            holder.itemView.context.startActivity(intent)
        }

        // Set the button icon based on the favorite state
        /*if (cuestionario.isFavorite) {
            holder.boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_50, 0, 0, 0)
        } else {
            holder.boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_border_50, 0, 0, 0)
        }

        holder.boton.setOnClickListener {
            // Toggle the favorite state
            cuestionario.isFavorite = !cuestionario.isFavorite

            // Update the button icon
            if (cuestionario.isFavorite) {
                holder.boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_50, 0, 0, 0)
            } else {
                holder.boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_favorite_border_50, 0, 0, 0)
            }
        }*/
    }

    override fun getItemCount() = cuestionarios.size
}