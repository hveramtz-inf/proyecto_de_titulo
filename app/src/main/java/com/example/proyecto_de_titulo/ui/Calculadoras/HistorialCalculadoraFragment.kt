package com.example.proyecto_de_titulo.ui.Calculadoras

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.dataApiRest.HistorialCalculadoraApi
import com.example.proyecto_de_titulo.dataApiRest.VariableHistorialApi
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistorialCalculadoraFragment : Fragment() {

    private val viewModel: HistorialCalculadoraViewModel by viewModels()
    private var calculadoraId: String? = null
    private lateinit var historial: List<HistorialCalculadoraApi>
    private lateinit var variables: List<VariableHistorialApi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculadoraId = arguments?.getString("calculadoraId")
        Log.d("HistorialCalculadoraFragment", "Received calculadoraId: $calculadoraId")
        val estudianteId = obtenerEstudianteId()
        buscarHistorialPorCalculadoraId(calculadoraId, estudianteId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial_calculadora, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.listadoHistorial)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HistorialCalculadoraAdapter(emptyList(), emptyList())
        return view
    }

    private fun obtenerEstudianteId(): String? {
        val sharedPreferences =
            requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        return sharedPreferences.getString("IdEstudiante", null)
    }

    private fun buscarHistorialPorCalculadoraId(calculadoraId: String?, estudianteId: String?) {
        val apiService = RetrofitClient.historialCalculadoraApiService
        val call = apiService.getHistorialCalculadoraByCalculadoraAndEstudiante(
            calculadoraId!!,
            estudianteId!!
        )

        call.enqueue(object : Callback<List<HistorialCalculadoraApi>> {
            override fun onResponse(
                call: Call<List<HistorialCalculadoraApi>>,
                response: Response<List<HistorialCalculadoraApi>>
            ) {
                if (response.isSuccessful) {
                    historial = response.body() ?: emptyList()
                    fetchVariablesForHistorial(historial)
                } else {
                    Log.e(
                        "HistorialCalculadoraFragment",
                        "Error in response: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<List<HistorialCalculadoraApi>>, t: Throwable) {
                Log.e("HistorialCalculadoraFragment", "Error fetching historial: ${t.message}")
            }
        })
    }

    private fun fetchVariablesForHistorial(listaHistoriales: List<HistorialCalculadoraApi>) {
        val apiService = RetrofitClient.variableHistorialApiService

        listaHistoriales.forEach { historial ->
            val call = apiService.getVariableHistorialByHistorial(historial.id.toString())

            call.enqueue(object : Callback<List<VariableHistorialApi>> {
                override fun onResponse(
                    call: Call<List<VariableHistorialApi>>,
                    response: Response<List<VariableHistorialApi>>
                ) {
                    if (response.isSuccessful) {
                        val variables = response.body() ?: emptyList()
                        // Update the RecyclerView adapter
                        view?.findViewById<RecyclerView>(R.id.listadoHistorial)?.adapter =
                            HistorialCalculadoraAdapter(listaHistoriales, variables)
                    } else {
                        Log.e(
                            "HistorialCalculadoraFragment",
                            "Error in response: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<List<VariableHistorialApi>>, t: Throwable) {
                    Log.e("HistorialCalculadoraFragment", "Error fetching variables: ${t.message}")
                }
            })
        }
    }
}

class HistorialCalculadoraAdapter(
    private val historialList: List<HistorialCalculadoraApi>,
    private val variables: List<VariableHistorialApi>
) : RecyclerView.Adapter<HistorialCalculadoraAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tituloFormulaLatex: WebView = view.findViewById(R.id.WebViewFormulaLatex)
        val tituloResultado: TextView = view.findViewById(R.id.tituloResultadoHistorial)
        val variablesRecyclerView: RecyclerView = view.findViewById(R.id.listadoVariablesHistorial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_calculadora, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val historial = historialList[position]
        renderLaTeX(holder, historial.formulalatex)
        holder.tituloResultado.text = "Resultado: " + historial.resultado.toString()

        val variablesAdapter = VariablesAdapter1(variables)
        holder.variablesRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.variablesRecyclerView.adapter = variablesAdapter
    }

    private fun renderLaTeX(holder: HistorialViewHolder, latex: String) {
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
        <body style="margin: 0px; padding-left: 0px; padding-right: 0px ; display: flex; justify-content: center; align-items: center; height: 100vh;">
            <div id="math-content" style="text-align: center;">
                $$ $latex $$
            </div>
        </body>
        </html>
        """.trimIndent()

        holder.tituloFormulaLatex.settings.javaScriptEnabled = true
        holder.tituloFormulaLatex.settings.loadWithOverviewMode = true
        holder.tituloFormulaLatex.settings.useWideViewPort = true
        holder.tituloFormulaLatex.loadDataWithBaseURL(null, mathJaxConfig, "text/html", "utf-8", null)
    }

    override fun getItemCount(): Int = historialList.size
}

class VariablesAdapter1(private val variablesList: List<VariableHistorialApi>) :
    RecyclerView.Adapter<VariablesAdapter1.VariableViewHolder>() {

    class VariableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewVariable: TextView = view.findViewById(R.id.tituloVariableHistorial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_variables_de_historial_calculadoras, parent, false)
        return VariableViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariableViewHolder, position: Int) {
        val variable = variablesList[position]
        holder.textViewVariable.text = "${variable.variable}: ${variable.valor}"
    }

    override fun getItemCount(): Int = variablesList.size
}