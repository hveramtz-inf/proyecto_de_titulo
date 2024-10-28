package com.example.proyecto_de_titulo.ui.Calculadoras

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.HistorialCalculadora
import com.example.proyecto_de_titulo.data.Variable
import com.example.proyecto_de_titulo.dataApiRest.HistorialCalculadoraApi
import com.example.proyecto_de_titulo.dataApiRest.VariableHistorialApi
import com.example.proyecto_de_titulo.interfazApiRest.HistorialCalculadoraApiService
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistorialCalculadoraFragment : Fragment() {

    private val viewModel: HistorialCalculadoraViewModel by viewModels()
    private var calculadoraId: String? = null
    private lateinit var historial: List<HistorialCalculadora>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculadoraId = arguments?.getString("calculadoraId")
        Log.d("HistorialCalculadoraFragment", "Received calculadoraId: $calculadoraId")
        historial = buscarHistorialPorCalculadoraId(calculadoraId)
        Log.d("HistorialCalculadoraFragment", "Historial retrieved: $historial")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial_calculadora, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HistorialCalculadoraAdapter(historial)
        return view
    }

    private fun buscarHistorialPorCalculadoraId(calculadoraId: String?): List<HistorialCalculadora> {
        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val estudianteId = sharedPreferences.getString("IdEstudiante", null) ?: return emptyList()

        val historialList = mutableListOf<HistorialCalculadora>()
        val apiService = RetrofitClient.historialCalculadoraApiService
        val call = apiService.getHistorialCalculadoraByCalculadoraAndEstudiante(calculadoraId!!, estudianteId)
        call.enqueue(object : Callback<List<HistorialCalculadoraApi>> {
            override fun onResponse(call: Call<List<HistorialCalculadoraApi>>, response: Response<List<HistorialCalculadoraApi>>) {
                if (response.isSuccessful) {
                    val historialApiList = response.body() ?: emptyList()
                    for (historialApi in historialApiList) {
                        historialList.add(HistorialCalculadora(
                            id = historialApi.id,
                            idcalculadora = historialApi.idcalculadora,
                            idestudiante = historialApi.idestudiante,
                            formulalatex = historialApi.formulalatex,
                            resultado = historialApi.resultado,
                            created_at = historialApi.created_at
                        ))
                    }
                }
            }

            override fun onFailure(call: Call<List<HistorialCalculadoraApi>>, t: Throwable) {
                Log.e("HistorialCalculadoraFragment", "Error fetching historial: ${t.message}")
            }
        })

        return historialList
    }
}

class HistorialCalculadoraAdapter(private val historialList: List<HistorialCalculadora>) :
    RecyclerView.Adapter<HistorialCalculadoraAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titutoFormula: TextView = view.findViewById(R.id.tituloFormulaHistorial)
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
        holder.titutoFormula.text = historial.formulalatex
        holder.tituloResultado.text = historial.resultado.toString()

        val variablesAdapter = VariablesAdapter1(historial.variables)
        holder.variablesRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.variablesRecyclerView.adapter = variablesAdapter
    }

    override fun getItemCount(): Int = historialList.size
}

class VariablesAdapter1(private val variablesList: List<Variable>) :
    RecyclerView.Adapter<VariablesAdapter1.VariableViewHolder>() {

    class VariableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewVariable: TextView = view.findViewById(R.id.tituloVariableHistorial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_variable_historial, parent, false)
        return VariableViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariableViewHolder, position: Int) {
        val variable = variablesList[position]
        holder.textViewVariable.text = "${variable.name}: ${variable.value}"
    }

    override fun getItemCount(): Int = variablesList.size
}

class CustomConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}