// ListaFavoritos.kt
package com.example.proyecto_de_titulo.ui.favoritos

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.dataApiRest.CalculadoraApi
import com.example.proyecto_de_titulo.dataApiRest.CuestionarioApi
import com.example.proyecto_de_titulo.dataApiRest.FavoritosCalculadora
import com.example.proyecto_de_titulo.dataApiRest.FavoritosCuestionario
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID



class ListaFavoritos : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritosAdapter
    private var favoritosCuestionarios: List<FavoritosCuestionario>? = null
    private var cuestionarios: List<CuestionarioApi>? = null
    private var favoritosCalculadora: List<FavoritosCalculadora>? = null

    @SuppressLint("MissingInflatedId")
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val view = inflater.inflate(R.layout.fragment_lista_favoritos, container, false)
            recyclerView = view.findViewById(R.id.listaCuestionariosFavoritos)
            recyclerView.layoutManager = LinearLayoutManager(context)

            val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
            val clavePucv = sharedPreferences.getString("ClavePucvid", null)
            Log.d("ClavePucv", clavePucv.toString())
            val idEstudiante = sharedPreferences.getString("IdEstudiante", null)

            val calculadoras = arguments?.getParcelableArrayList<CalculadoraApi>("calculadoras")
            favoritosCalculadora = arguments?.getParcelableArrayList("favoritosCalculadora")

            Log.d("Calculadoras", calculadoras.toString())

            adapter = FavoritosAdapter(cuestionarios, favoritosCuestionarios, calculadoras, favoritosCalculadora, this)

            if (idEstudiante != null) {
                if (clavePucv != null) {
                    obtenerCuestionarios(clavePucv)
                }
                obtenerCuestionariosFavoritos(idEstudiante)
            } else {
                Toast.makeText(context, "ClavePucv not found in SharedPreferences", Toast.LENGTH_SHORT).show()
            }
            recyclerView.adapter = adapter

            return view
        }

    fun addFavoritoCuestionario(idCuestionario: UUID, holder: FavoritosAdapter.CuestionarioViewHolder) {
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null) ?: return

        val favorito = FavoritosCuestionario(idestudiante = UUID.fromString(idEstudiante), idcuestionario = idCuestionario)
        RetrofitClient.favoritoCuestionarioApiService.createFavoritoCuestionario(favorito).enqueue(object :
            Callback<FavoritosCuestionario> {
            override fun onResponse(call: Call<FavoritosCuestionario>, response: Response<FavoritosCuestionario>) {
                if (response.isSuccessful) {
                    fetchFavoritosCuestionarios()
                } else {
                    Toast.makeText(requireContext(), "Failed to add favorite", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavoritosCuestionario>, t: Throwable) {
                Toast.makeText(requireContext(), "Error adding favorite", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteFavoritoCuestionario(idCuestionario: UUID, holder: FavoritosAdapter.CuestionarioViewHolder) {
        val favorito = favoritosCuestionarios?.find { it.idcuestionario == idCuestionario } ?: return
        RetrofitClient.favoritoCuestionarioApiService.deleteFavoritoCuestionario(favorito.id.toString()).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    fetchFavoritosCuestionarios()
                } else {
                    Toast.makeText(requireContext(), "Failed to remove favorite", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error removing favorite", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun obtenerCuestionarios(clavePucv: String) {
        RetrofitClient.cuestionarioApiService.getCuestionariosByClavePucv(clavePucv).enqueue(object : Callback<List<CuestionarioApi>> {
            override fun onResponse(call: Call<List<CuestionarioApi>>, response: Response<List<CuestionarioApi>>) {
                if (response.isSuccessful) {
                    cuestionarios = response.body()
                    if (cuestionarios != null) {
                        Log.d("Cuestionarios", "Cuestionarios loaded: ${cuestionarios.toString()}")
                        adapter.updateCuestionarios(cuestionarios!!)
                    } else {
                        Log.d("Cuestionarios", "Cuestionarios list is null")
                    }
                } else {
                    Toast.makeText(context, "Failed to load cuestionarios", Toast.LENGTH_SHORT).show()
                    Log.d("Cuestionarios", "Failed to load cuestionarios: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<CuestionarioApi>>, t: Throwable) {
                Toast.makeText(context, "Error loading cuestionarios", Toast.LENGTH_SHORT).show()
                Log.d("Cuestionarios", "Error loading cuestionarios: ${t.message}")
            }
        })
    }

    fun obtenerCuestionariosFavoritos(idEstudiante: String) {
        RetrofitClient.favoritoCuestionarioApiService.getFavoritosCuestionarioByEstudiante(idEstudiante).enqueue(object : Callback<List<FavoritosCuestionario>> {
            override fun onResponse(call: Call<List<FavoritosCuestionario>>, response: Response<List<FavoritosCuestionario>>) {
                if (response.isSuccessful) {
                    favoritosCuestionarios = response.body()
                    fetchFavoritosCuestionarios()
                } else {
                    Toast.makeText(context, "Failed to load favoritos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FavoritosCuestionario>>, t: Throwable) {
                Toast.makeText(context, "Error loading favoritos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun addFavoritoCalculadora(idCalculadora: UUID, holder: FavoritosAdapter.CalculadoraViewHolder) {
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null) ?: return

        val favorito = FavoritosCalculadora(idestudiante = UUID.fromString(idEstudiante), idcalculadora = idCalculadora)
        RetrofitClient.favoritoCalculadoraApiService.createFavoritoCalculadora(favorito).enqueue(object : Callback<FavoritosCalculadora> {
            override fun onResponse(call: Call<FavoritosCalculadora>, response: Response<FavoritosCalculadora>) {
                if (response.isSuccessful) {
                    fetchFavoritosCalculadora()
                } else {
                    Toast.makeText(requireContext(), "Failed to add favorite", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FavoritosCalculadora>, t: Throwable) {
                Toast.makeText(requireContext(), "Error adding favorite", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteFavoritoCalculadora(idCalculadora: UUID, holder: FavoritosAdapter.CalculadoraViewHolder) {
        val favorito = favoritosCalculadora?.find { it.idcalculadora == idCalculadora } ?: return
        RetrofitClient.favoritoCalculadoraApiService.deleteFavoritoCalculadora(favorito.id.toString()).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    fetchFavoritosCalculadora()
                } else {
                    Toast.makeText(requireContext(), "Failed to remove favorite", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error removing favorite", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchFavoritosCuestionarios() {
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null)

        if (idEstudiante != null) {
            RetrofitClient.favoritoCuestionarioApiService.getFavoritosCuestionarioByEstudiante(idEstudiante).enqueue(object : Callback<List<FavoritosCuestionario>> {
                override fun onResponse(call: Call<List<FavoritosCuestionario>>, response: Response<List<FavoritosCuestionario>>) {
                    if (response.isSuccessful) {
                        favoritosCuestionarios = response.body() ?: emptyList()
                        Log.d("FavoritosCuestionarios", "Favoritos loaded: ${favoritosCuestionarios.toString()}")
                        adapter.updateFavoritosCuestionarios(favoritosCuestionarios!!)
                    } else {
                        Toast.makeText(context, "Failed to load favoritos", Toast.LENGTH_SHORT).show()
                        Log.d("FavoritosCuestionarios", "Failed to load favoritos: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<FavoritosCuestionario>>, t: Throwable) {
                    Toast.makeText(context, "Error loading favoritos", Toast.LENGTH_SHORT).show()
                    Log.d("FavoritosCuestionarios", "Error loading favoritos: ${t.message}")
                }
            })
        } else {
            Toast.makeText(context, "idEstudiante not found in SharedPreferences", Toast.LENGTH_SHORT).show()
            Log.d("FavoritosCuestionarios", "idEstudiante not found in SharedPreferences")
        }
    }

    private fun fetchFavoritosCalculadora() {
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null) ?: return

        RetrofitClient.favoritoCalculadoraApiService.getFavoritosCalculadoraByEstudiante(idEstudiante).enqueue(object : Callback<List<FavoritosCalculadora>> {
            override fun onResponse(call: Call<List<FavoritosCalculadora>>, response: Response<List<FavoritosCalculadora>>) {
                if (response.isSuccessful) {
                    favoritosCalculadora = response.body()
                    if (favoritosCalculadora != null) {
                        adapter.updateFavoritosCalculadora(favoritosCalculadora!!)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load favoritos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FavoritosCalculadora>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error loading favoritos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// FavoritosAdapter.kt
class FavoritosAdapter(
    private var cuestionarios: List<CuestionarioApi>?,
    private var favoritosCuestionarios: List<FavoritosCuestionario>?,
    private var calculadoras: List<CalculadoraApi>?,
    private var favoritosCalculadora: List<FavoritosCalculadora>?,
    private val fragment: ListaFavoritos
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filteredCuestionarios: List<CuestionarioApi> = emptyList()
    private var filteredCalculadoras: List<CalculadoraApi> = emptyList()

    companion object {
        const val VIEW_TYPE_CUESTIONARIO = 0
        const val VIEW_TYPE_CALCULADORA = 1
    }

    init {
        updateFilteredLists()
    }

    private fun updateFilteredLists() {
        filteredCuestionarios = cuestionarios?.filter { cuestionario ->
            favoritosCuestionarios?.any { it.idcuestionario == cuestionario.id } ?: false
        } ?: emptyList()

        filteredCalculadoras = calculadoras?.filter { calculadora ->
            favoritosCalculadora?.any { favorito -> favorito.idcalculadora == calculadora.id } ?: false
        } ?: emptyList()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < filteredCuestionarios.size) {
            VIEW_TYPE_CUESTIONARIO
        } else {
            VIEW_TYPE_CALCULADORA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CUESTIONARIO) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.tarjeta_cuestionarios, parent, false)
            CuestionarioViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.tarjeta_de_calculadoras, parent, false)
            CalculadoraViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CuestionarioViewHolder) {
            val cuestionario = filteredCuestionarios[position]
            holder.tituloCuestionario.text = cuestionario.titulo

            val isFavorito = favoritosCuestionarios?.any { it.idcuestionario == cuestionario.id } ?: false
            updateFavoriteIcon(holder.botonFavoritoCuestionario, isFavorito)
            holder.botonFavoritoCuestionario.setOnClickListener {
                if (isFavorito) {
                    fragment.deleteFavoritoCuestionario(cuestionario.id, holder)
                } else {
                    fragment.addFavoritoCuestionario(cuestionario.id, holder)
                }
            }
        } else if (holder is CalculadoraViewHolder) {
            val calculadora = filteredCalculadoras[position - filteredCuestionarios.size]
            holder.tituloCalculadora.text = calculadora.nombre

            val isFavorito = favoritosCalculadora?.any { it.idcalculadora == calculadora.id } ?: false
            updateFavoriteIcon(holder.botonFavoritoCalculadora, isFavorito)
            holder.botonFavoritoCalculadora.setOnClickListener {
                if (isFavorito) {
                    fragment.deleteFavoritoCalculadora(calculadora.id, holder)
                } else {
                    fragment.addFavoritoCalculadora(calculadora.id, holder)
                }
            }
            calculadora.latexformula?.let { renderLaTeX(holder, it) }
        }
    }

    private fun renderLaTeX(holder: CalculadoraViewHolder, latex: String) {
        val mathJaxConfig = """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
            <script type="text/javascript" async
                src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML">
            </script>
            <script type="text/x-mathjax-config">
                MathJax.Hub.Config({
                    tex2jax: {inlineMath: [['$','$'], ['\\(','\\)']]},
                    "HTML-CSS": { scale: 150, linebreaks: { automatic: true } },
                    SVG: { scale: 150, linebreaks: { automatic: true } }
                });
            </script>
        </head>
        <body style="margin: 0px; padding-left: 30px; padding-right: 0px ; display: flex; justify-content: center; align-items: center; height: 100vh;">
            <div id="math-content" style="text-align: center;">
                $$ $latex $$
            </div>
        </body>
        </html>
        """.trimIndent()

        holder.formulaWebView.settings.javaScriptEnabled = true
        holder.formulaWebView.settings.loadWithOverviewMode = true
        holder.formulaWebView.settings.useWideViewPort = true
        holder.formulaWebView.loadDataWithBaseURL(null, mathJaxConfig, "text/html", "utf-8", null)
    }

    private fun updateFavoriteIcon(button: Button?, isFavorite: Boolean) {
        button?.setCompoundDrawablesWithIntrinsicBounds(
            if (isFavorite) R.drawable.baseline_favorite_50 else R.drawable.baseline_favorite_border_50, 0, 0, 0
        )
    }

    override fun getItemCount(): Int {
        return filteredCuestionarios.size + filteredCalculadoras.size
    }

    fun updateFavoritosCuestionarios(favoritos: List<FavoritosCuestionario>) {
        this.favoritosCuestionarios = favoritos
        updateFilteredLists()
        notifyDataSetChanged()
    }

    fun updateFavoritosCalculadora(favoritos: List<FavoritosCalculadora>) {
        this.favoritosCalculadora = favoritos
        updateFilteredLists()
        notifyDataSetChanged()
    }

    fun updateCuestionarios(cuestionarios: List<CuestionarioApi>) {
        this.cuestionarios = cuestionarios
        updateFilteredLists()
        notifyDataSetChanged()
    }

    class CuestionarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloCuestionario: Button = itemView.findViewById(R.id.intentarCuestionario)
        val botonFavoritoCuestionario: Button = itemView.findViewById(R.id.guardarFavoritoCuestionario)
    }

    class CalculadoraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val formulaWebView: WebView = itemView.findViewById(R.id.FormulaTarjetaCalculadora)
        val tituloCalculadora: TextView = itemView.findViewById(R.id.TituloCalculadora)
        val botonFavoritoCalculadora: Button = itemView.findViewById(R.id.guardarFavoritoCalculadora)
    }
}