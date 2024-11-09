// CuestionariosFragment.kt
package com.example.proyecto_de_titulo.ui.Cuestionarios

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
    private val listaCuestionaros: MutableList<CuestionarioApi> = mutableListOf()
    private val listaFavoritoCuestionarios: MutableList<FavoritosCuestionario> = mutableListOf()
    private val listaPuntajes: MutableList<PuntajeAlumnoCuestionario> = mutableListOf()
    private val cursos: MutableList<CursoApi?> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCuestionariosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the adapter with an empty list
        cuestionariosAdapter = CursoAdapter(emptyList(), emptyList(), emptyList(), emptyList())

        // Configure the RecyclerView with a LayoutManager and the adapter
        binding.listadoCuestionarios.layoutManager = LinearLayoutManager(context)
        binding.listadoCuestionarios.adapter = cuestionariosAdapter

        // Show the loading item before making the API call
        showLoading()

        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null)
        val clavePucv = sharedPreferences.getString("ClavePucvid", null)
        obtenerCuestionarios(clavePucv.toString())

        // Fetch cursos from API
        obtenerCursos()
        obtenerPuntajeAlumno()
        obtenerFavoritosCuestionarios()

        val botonListaFavoritosCuestionarios: Button = binding.root.findViewById(R.id.botonIrFavCuestionario)

        botonListaFavoritosCuestionarios.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("cuestionarios", ArrayList(listaCuestionaros))
                putParcelableArrayList("favoritosCuestionarios", ArrayList(listaFavoritoCuestionarios))
            }
            findNavController().navigate(R.id.navigation_favoritosCuestionarios, bundle)
        }

        val buscadorCuestionarios: EditText = binding.root.findViewById(R.id.buscadorCuestionario)
        val botonBuscarCuestionarios: Button = binding.root.findViewById(R.id.buscarCuestionario)

        botonBuscarCuestionarios.setOnClickListener {
            val textoBuscado = buscadorCuestionarios.text.toString()
            val cuestionariosFiltrados = listaCuestionaros.filter { it.titulo.contains(textoBuscado, ignoreCase = true) }
            cuestionariosAdapter.updateData(cursos, cuestionariosFiltrados, listaPuntajes, listaFavoritoCuestionarios)
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
                        val puntajes = response.body() ?: emptyList()
                        listaPuntajes.clear()
                        listaPuntajes.addAll(puntajes)
                        cuestionariosAdapter.updateData(cursos, listaCuestionaros, listaPuntajes, listaFavoritoCuestionarios)
                    } else {
                        Toast.makeText(context, "Error loading puntajes", Toast.LENGTH_SHORT).show()
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
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val clavePucv = sharedPreferences.getString("ClavePucvid", null)

        val apiService = RetrofitClient.cursoApiService
        if (clavePucv != null) {
            apiService.getCursoByClavePucv(clavePucv).enqueue(object : Callback<List<CursoApi>> {
                override fun onResponse(call: Call<List<CursoApi>>, response: Response<List<CursoApi>>) {
                    hideLoading()
                    if (response.isSuccessful) {
                        val cursosList = response.body()?.filter { !it.ocultar } ?: emptyList() // Filter out courses with ocultar == true
                        cursos.clear()
                        cursos.addAll(cursosList)
                        cuestionariosAdapter.updateData(cursos, listaCuestionaros, listaPuntajes, listaFavoritoCuestionarios)
                    } else {
                        Toast.makeText(context, "Error loading courses", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CursoApi>>, t: Throwable) {
                    hideLoading()
                    Toast.makeText(context, "Error loading courses", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun obtenerCuestionarios(clavepucvid: String) {
        val apiService = RetrofitClient.cuestionarioApiService
        apiService.getCuestionariosByClavePucv(clavepucvid).enqueue(object : Callback<List<CuestionarioApi>> {
            override fun onResponse(call: Call<List<CuestionarioApi>>, response: Response<List<CuestionarioApi>>) {
                if (response.isSuccessful) {
                    val cuestionarios = response.body()?.filter { cuestionario ->
                        val curso = cursos.find { it?.id == cuestionario.idcurso }
                        curso != null && !curso.ocultar
                    } ?: emptyList() // Filter out items with ocultar == true
                    listaCuestionaros.clear()
                    listaCuestionaros.addAll(cuestionarios)
                    cuestionariosAdapter.updateData(cursos, listaCuestionaros, listaPuntajes, listaFavoritoCuestionarios)
                } else {
                    Toast.makeText(context, "Error loading cuestionarios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CuestionarioApi>>, t: Throwable) {
                Toast.makeText(context, "Error loading cuestionarios", Toast.LENGTH_SHORT).show()
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
                        val favoritos = response.body() ?: emptyList()
                        listaFavoritoCuestionarios.clear()
                        listaFavoritoCuestionarios.addAll(favoritos)
                        cuestionariosAdapter.updateData(cursos, listaCuestionaros, listaPuntajes, listaFavoritoCuestionarios)
                    } else {
                        Toast.makeText(context, "Error loading favoritos", Toast.LENGTH_SHORT).show()
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
        cuestionariosAdapter.updateData(listOf(null), emptyList(), emptyList(), emptyList())
    }

    private fun hideLoading() {
        cuestionariosAdapter.updateData(emptyList(), emptyList(), emptyList(), emptyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// CursoAdapter.kt
class CursoAdapter(
    private var cursos: List<CursoApi?>,
    private var cuestionarios: List<CuestionarioApi>,
    private var puntajes: List<PuntajeAlumnoCuestionario>,
    private var favoritosCuestionarios: List<FavoritosCuestionario>
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
            holder.contenedorTituloCuestionario.text = curso?.nombre ?: "Loading..."

            // Filter cuestionarios by curso.id
            val filteredCuestionarios = cuestionarios.filter { it.idcurso == curso?.id }

            // Set up the nested RecyclerView for filtered cuestionarios
            holder.listadoDeCuestionarios.layoutManager = LinearLayoutManager(holder.itemView.context)
            val nestedAdapter = NestedCuestionariosAdapter(filteredCuestionarios, puntajes, favoritosCuestionarios)
            holder.listadoDeCuestionarios.adapter = nestedAdapter
            holder.listadoDeCuestionarios.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun getItemCount() = cursos.size

    override fun getItemViewType(position: Int): Int {
        return if (cursos[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    fun updateData(newCursos: List<CursoApi?>, newCuestionarios: List<CuestionarioApi>, newPuntajes: List<PuntajeAlumnoCuestionario>, newFavoritos: List<FavoritosCuestionario>) {
        this.cursos = newCursos
        this.cuestionarios = newCuestionarios
        this.puntajes = newPuntajes
        this.favoritosCuestionarios = newFavoritos
        notifyDataSetChanged()
    }
}

// NestedCuestionariosAdapter.kt
class NestedCuestionariosAdapter(
    private var cuestionarios: List<CuestionarioApi>,
    private val puntajes: List<PuntajeAlumnoCuestionario>,
    private val favoritosCuestionarios: List<FavoritosCuestionario>
) : RecyclerView.Adapter<NestedCuestionariosAdapter.NestedViewHolder>() {

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
        val cuestionario = cuestionarios[position]
        holder.preguntaTextView.text = cuestionario.titulo

        val puntaje = puntajes.find { it.idcuestionario == cuestionario.id }?.puntaje ?: 0f
        holder.progressBar.max = 100
        holder.progressBar.progress = puntaje.toInt()


        val isFavorito = favoritosCuestionarios.any { it.idcuestionario == cuestionario.id }
        holder.guardarFavoritoButton.setBackgroundResource(
            if (isFavorito) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )

        holder.preguntaTextView.setOnClickListener{
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

    override fun getItemCount() = cuestionarios.size

    private fun addFavorito(idCuestionario: UUID, holder: NestedViewHolder) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null) ?: return

        val favorito = FavoritosCuestionario(idestudiante = UUID.fromString(idEstudiante), idcuestionario = idCuestionario)
        RetrofitClient.favoritoCuestionarioApiService.createFavoritoCuestionario(favorito).enqueue(object : Callback<FavoritosCuestionario> {
            override fun onResponse(call: Call<FavoritosCuestionario>, response: Response<FavoritosCuestionario>) {
                if (response.isSuccessful) {
                    obtenerFavoritosCuestionarios(holder)
                } else {
                    Toast.makeText(holder.itemView.context, "Error adding favorito", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavoritosCuestionario>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "Error adding favorito", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(holder.itemView.context, "Error deleting favorito", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "Error deleting favorito", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerFavoritosCuestionarios(holder: NestedViewHolder) {
        val sharedPreferences = holder.itemView.context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null) ?: return

        RetrofitClient.favoritoCuestionarioApiService.getFavoritosCuestionarioByEstudiante(idEstudiante).enqueue(object : Callback<List<FavoritosCuestionario>> {
            override fun onResponse(call: Call<List<FavoritosCuestionario>>, response: Response<List<FavoritosCuestionario>>) {
                if (response.isSuccessful) {
                    val favoritos = response.body() ?: emptyList()
                    updateFavoritos(favoritos)
                } else {
                    Toast.makeText(holder.itemView.context, "Error loading favoritos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FavoritosCuestionario>>, t: Throwable) {
                Toast.makeText(holder.itemView.context, "Error loading favoritos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateFavoritos(favoritos: List<FavoritosCuestionario>) {
        (this.favoritosCuestionarios as MutableList).clear()
        (this.favoritosCuestionarios as MutableList).addAll(favoritos)
        notifyDataSetChanged()
    }

}