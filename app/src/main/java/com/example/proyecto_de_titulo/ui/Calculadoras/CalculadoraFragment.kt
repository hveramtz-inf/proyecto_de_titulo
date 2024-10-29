// CalculadoraFragment.kt
package com.example.proyecto_de_titulo.ui.Calculadoras

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.dataApiRest.CalculadoraApi
import com.example.proyecto_de_titulo.dataApiRest.FavoritosCalculadora
import com.example.proyecto_de_titulo.dataApiRest.calculadorasList
import com.example.proyecto_de_titulo.databinding.FragmentCalculadorasBinding
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


class CalculadoraFragment : Fragment() {

    private var _binding: FragmentCalculadorasBinding? = null
    private val binding get() = _binding!!

    private lateinit var calculadorasList: List<CalculadoraApi>
    var favoritosCalculadora: List<FavoritosCalculadora> = emptyList() // Initialize with an empty list

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
            bundle.putParcelableArrayList("favoritosCalculadora", ArrayList(favoritosCalculadora))

            findNavController().navigate(R.id.navigation_favoritosCuestionarios, bundle)
        }

        val listadoCalculadora = root.findViewById<RecyclerView>(R.id.listadoCalculadoras)
        listadoCalculadora.layoutManager = LinearLayoutManager(context)

        val navController = findNavController()
        listadoCalculadora.adapter = CalculadoraAdapter(emptyList(), navController, this) // Initialize with an empty list

        val buscadorCalculadora = binding.root.findViewById<EditText>(R.id.buscadorCalculadoras)
        val botonBuscar = binding.root.findViewById<Button>(R.id.buscarCalculadora)

        botonBuscar.setOnClickListener {
            val busqueda = buscadorCalculadora.text.toString()
            val calculadorasFiltradas = calculadorasList.filter {
                it.nombre?.contains(busqueda, ignoreCase = true) ?: false
            }
            listadoCalculadora.adapter = CalculadoraAdapter(calculadorasFiltradas, navController, this)
        }
        fetchCalculadoras() // Fetch calculators from the API
        fetchFavoritosCalculadora() // Fetch favoritos from the API

        return root
    }

    private fun fetchCalculadoras() {
        val apiService = RetrofitClient.calculadoraApiService
        apiService.getCalculadoras().enqueue(object : Callback<List<CalculadoraApi>> {
            override fun onResponse(call: Call<List<CalculadoraApi>>, response: Response<List<CalculadoraApi>>) {
                if (response.isSuccessful) {
                    calculadorasList = response.body() ?: emptyList()
                    updateCalculadoraList(calculadorasList)
                } else {
                    Log.e("CalculadoraFragment", "Failed to fetch calculators")
                }
            }

            override fun onFailure(call: Call<List<CalculadoraApi>>, t: Throwable) {
                Log.e("CalculadoraFragment", "Error fetching calculators", t)
            }
        })
    }

    private fun fetchFavoritosCalculadora() {
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null) ?: return

        RetrofitClient.favoritoCalculadoraApiService.getFavoritosCalculadoraByEstudiante(idEstudiante).enqueue(object : Callback<List<FavoritosCalculadora>> {
            override fun onResponse(call: Call<List<FavoritosCalculadora>>, response: Response<List<FavoritosCalculadora>>) {
                if (response.isSuccessful) {
                    favoritosCalculadora = response.body() ?: emptyList()
                    updateCalculadoraList(calculadorasList)
                } else {
                    Log.e("CalculadoraFragment", "Failed to fetch favoritos")
                }
            }

            override fun onFailure(call: Call<List<FavoritosCalculadora>>, t: Throwable) {
                Log.e("CalculadoraFragment", "Error fetching favoritos", t)
            }
        })
    }

    private fun updateCalculadoraList(calculadoras: List<CalculadoraApi>) {
        val navController = findNavController()
        val adapter = CalculadoraAdapter(calculadoras, navController, this)
        binding.listadoCalculadoras.adapter = adapter
    }

    fun addFavorito(idCalculadora: UUID) {
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

    fun deleteFavorito(idCalculadora: UUID) {
        val favorito = favoritosCalculadora.find { it.idcalculadora == idCalculadora } ?: return
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class CalculadoraAdapter(
    private val items: List<CalculadoraApi>,
    private val navController: NavController,
    private val fragment: CalculadoraFragment
) : RecyclerView.Adapter<CalculadoraAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.TituloCalculadora)
        val FormulaWebView: WebView = view.findViewById(R.id.FormulaTarjetaCalculadora)
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
        renderLaTeX(holder, item.latexformula ?: "")

        val isFavorito = fragment.favoritosCalculadora.any { it.idcalculadora == item.id }
        holder.botonFavorito.setBackgroundResource(
            if (isFavorito) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )

        holder.botonFavorito.setOnClickListener {
            if (isFavorito) {
                fragment.deleteFavorito(item.id)
            } else {
                fragment.addFavorito(item.id)
            }
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("calculadoraItem", item)
            navController.navigate(R.id.navigation_usarCalculadoraFragment, bundle)
        }
    }

    private fun renderLaTeX(holder: ViewHolder, latex: String) {
        Log.d("Latex", latex)
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

        holder.FormulaWebView.settings.javaScriptEnabled = true
        holder.FormulaWebView.settings.loadWithOverviewMode = true
        holder.FormulaWebView.settings.useWideViewPort = true
        holder.FormulaWebView.loadDataWithBaseURL(null, mathJaxConfig, "text/html", "utf-8", null)
    }

    override fun getItemCount(): Int = items.size
}