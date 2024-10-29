package com.example.proyecto_de_titulo.ui.Cuestionarios

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.dataApiRest.CuestionarioApi
import com.example.proyecto_de_titulo.dataApiRest.CursoApi
import com.example.proyecto_de_titulo.dataApiRest.FavoritosCuestionario
import com.example.proyecto_de_titulo.dataApiRest.PuntajeAlumnoCuestionario
import com.example.proyecto_de_titulo.databinding.FragmentCuestionariosBinding
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class CuestionariosFragment : Fragment() {

    private var _binding: FragmentCuestionariosBinding? = null
    private val binding get() = _binding!!
    private lateinit var cuestionariosAdapter: CursoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuestionariosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the adapter with an empty list
        cuestionariosAdapter = CursoAdapter(emptyList())

        // Configure the RecyclerView with a LayoutManager and the adapter
        binding.listadoCuestionarios.layoutManager = LinearLayoutManager(context)
        binding.listadoCuestionarios.adapter = cuestionariosAdapter

        // Show the loading item before making the API call
        showLoading()

        // Fetch cursos from API
        obtenerCursos()
        obtenerPuntajeAlumno()
        obtenerFavoritosCuestionarios()

        val botonListaFavoritosCuestionarios: Button = binding.root.findViewById(R.id.botonIrFavCuestionario)
        botonListaFavoritosCuestionarios.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList("cuestionarios", ArrayList(cuestionariosList))
            bundle.putParcelableArrayList("favoritosCuestionarios", ArrayList(favoritosCuestionarios))

            findNavController().navigate(R.id.navigation_favoritosCuestionarios, bundle)
        }
        return root
    }

    private fun obtenerPuntajeAlumno() {
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null)

        if (idEstudiante != null) {
            val apiService = RetrofitClient.puntajeAlumnoCuestionarioApiService
            apiService.getPuntajesByEstudiante(idEstudiante).enqueue(object : Callback<List<PuntajeAlumnoCuestionario>> {
                override fun onResponse(call: Call<List<PuntajeAlumnoCuestionario>>, response: Response<List<PuntajeAlumnoCuestionario>>) {
                    if (response.isSuccessful) {
                        val puntajes = response.body()
                        if (puntajes != null) {
                            // Update the adapter with the new puntajes
                            cuestionariosAdapter.updatePuntajes(puntajes)
                        }
                    } else {
                        Toast.makeText(context, "Failed to load puntajes", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<PuntajeAlumnoCuestionario>>, t: Throwable) {
                    Toast.makeText(context, "Error loading puntajes", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "idAlumno not found in SharedPreferences", Toast.LENGTH_SHORT).show()
        }
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

    private fun obtenerFavoritosCuestionarios() {
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null)

        if (idEstudiante != null) {
            val apiService = RetrofitClient.favoritoCuestionarioApiService
            apiService.getFavoritosCuestionarioByEstudiante(idEstudiante).enqueue(object : Callback<List<FavoritosCuestionario>> {
                override fun onResponse(call: Call<List<FavoritosCuestionario>>, response: Response<List<FavoritosCuestionario>>) {
                    if (response.isSuccessful) {
                        val favoritos = response.body()
                        if (favoritos != null) {
                            cuestionariosAdapter.updateFavoritos(favoritos)
                        }
                    } else {
                        Toast.makeText(context, "Failed to load favoritos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<FavoritosCuestionario>>, t: Throwable) {
                    Toast.makeText(context, "Error loading favoritos", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "idAlumno not found in SharedPreferences", Toast.LENGTH_SHORT).show()
        }
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

class CursoAdapter(
    private var cursos: List<CursoApi?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var puntajes: List<PuntajeAlumnoCuestionario> = emptyList()
    private var favoritosCuestionarios: List<FavoritosCuestionario> = emptyList()

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
                            val nestedAdapter = NestedCuestionariosAdapter(cuestionarios, puntajes)
                            nestedAdapter.updateFavoritos(favoritosCuestionarios)
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

    fun updatePuntajes(newPuntajes: List<PuntajeAlumnoCuestionario>) {
        this.puntajes = newPuntajes
        notifyDataSetChanged()
    }

    fun updateFavoritos(favoritos: List<FavoritosCuestionario>) {
        this.favoritosCuestionarios = favoritos
        notifyDataSetChanged()
    }
}

class NestedCuestionariosAdapter(
    private var cuestionarios: List<CuestionarioApi>,
    private val puntajes: List<PuntajeAlumnoCuestionario>
) : RecyclerView.Adapter<NestedCuestionariosAdapter.NestedViewHolder>() {

    private var filteredCuestionarios: List<CuestionarioApi> = cuestionarios
    private var favoritosCuestionarios: List<FavoritosCuestionario> = emptyList()

    class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preguntaTextView: Button = itemView.findViewById(R.id.intentarCuestionario)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val guardarFavoritoButton: Button = itemView.findViewById(R.id.guardarFavoritoCuestionario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_cuestionarios, parent, false)
        return NestedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        val cuestionario = filteredCuestionarios[position]
        holder.preguntaTextView.text = (position + 1).toString()

        val puntaje = puntajes.find { it.idcuestionario == cuestionario.id }?.puntaje ?: 0f
        holder.progressBar.max = 100
        holder.progressBar.progress = puntaje.toInt()

        val isFavorito = favoritosCuestionarios.any { it.idcuestionario == cuestionario.id }
        holder.guardarFavoritoButton.setBackgroundResource(
            if (isFavorito) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )

        holder.preguntaTextView.setOnClickListener {
            val intent = Intent(holder.itemView.context, IntentarCuestionario::class.java)
            intent.putExtra("idCuestionario", cuestionario.id.toString())
            holder.itemView.context.startActivity(intent)
        }

        holder.guardarFavoritoButton.setOnClickListener {
            if (isFavorito) {
                deleteFavorito(cuestionario.id, holder)
            } else {
                addFavorito(cuestionario.id, holder)
            }
        }
    }

    override fun getItemCount() = filteredCuestionarios.size

    fun updateFavoritos(favoritos: List<FavoritosCuestionario>) {
        this.favoritosCuestionarios = favoritos
        notifyDataSetChanged()
    }

    private fun addFavorito(idCuestionario: UUID, holder: NestedViewHolder) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null) ?: return

        val favorito = FavoritosCuestionario(idestudiante = UUID.fromString(idEstudiante), idcuestionario = idCuestionario)
        RetrofitClient.favoritoCuestionarioApiService.createFavoritoCuestionario(favorito).enqueue(object : Callback<FavoritosCuestionario> {
            override fun onResponse(call: Call<FavoritosCuestionario>, response: Response<FavoritosCuestionario>) {
                if (response.isSuccessful) {
                    obtenerFavoritosCuestionarios(holder)
                } else {
                    Toast.makeText(holder.itemView.context, "Failed to add favorite", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavoritosCuestionario>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "Error adding favorite", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteFavorito(idCuestionario: UUID, holder: NestedViewHolder) {
        val favorito = favoritosCuestionarios.find { it.idcuestionario == idCuestionario } ?: return
        RetrofitClient.favoritoCuestionarioApiService.deleteFavoritoCuestionario(favorito.id.toString()).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    obtenerFavoritosCuestionarios(holder)
                } else {
                    Toast.makeText(holder.itemView.context, "Failed to remove favorite", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "Error removing favorite", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerFavoritosCuestionarios(holder: NestedViewHolder) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null) ?: return

        RetrofitClient.favoritoCuestionarioApiService.getFavoritosCuestionarioByEstudiante(idEstudiante).enqueue(object : Callback<List<FavoritosCuestionario>> {
            override fun onResponse(call: Call<List<FavoritosCuestionario>>, response: Response<List<FavoritosCuestionario>>) {
                if (response.isSuccessful) {
                    val favoritos = response.body()
                    if (favoritos != null) {
                        updateFavoritos(favoritos)
                    }
                } else {
                    Toast.makeText(holder.itemView.context, "Failed to load favoritos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FavoritosCuestionario>>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "Error loading favoritos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}