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

// In HistorialCalculadoraFragment.kt
class HistorialCalculadoraFragment : Fragment() {

    private val viewModel: HistorialCalculadoraViewModel by viewModels()
    private var calculadoraId: Int? = null
    private lateinit var historial: List<HistorialCalculadora>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculadoraId = arguments?.getInt("calculadoraId")
        Log.d("HistorialCalculadoraFragment", "Received calculadoraId: $calculadoraId")
        historial = buscarHistorialPorCalculadoraId(calculadoraId)
        Log.d("HistorialCalculadoraFragment", "Historial retrieved: $historial")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_historial_calculadora, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.listadoHistorial)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = HistorialCalculadoraAdapter(historial)
        return view
    }

    private fun buscarHistorialPorCalculadoraId(calculadoraId: Int?): List<HistorialCalculadora> {
        return HistorialCalculadora.getHistorial(calculadoraId!!)
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
        holder.titutoFormula.text = historial.formula
        holder.tituloResultado.text = historial.resultado.toString()

        // Configura el RecyclerView interno con un LinearLayoutManager vertical
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
            .inflate(R.layout.item_variables_de_historial_calculadoras, parent, false)
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
        // No interceptar eventos de toque, dejarlos pasar a las vistas hijas
        return false
    }
}
