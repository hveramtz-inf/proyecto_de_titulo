package com.example.proyecto_de_titulo.ui.Calculadoras

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.DataCalculadoras
import com.example.proyecto_de_titulo.data.HistorialCalculadora
import com.example.proyecto_de_titulo.data.Variable
import com.example.proyecto_de_titulo.data.calculadorasList
import net.objecthunter.exp4j.ExpressionBuilder

class UsarCalculadoraFragment : Fragment() {

    private lateinit var variablesRecyclerView: RecyclerView
    private lateinit var calculateButton: Button
    private var calculadoraId: Int? = null
    private var calculadora: DataCalculadoras? = null
    private val variables = mutableListOf<Variable>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usar_calculadora, container, false)
        variablesRecyclerView = view.findViewById(R.id.listadoDeVariables)
        calculateButton = view.findViewById(R.id.botonCalcular)
        val Tituloformula: TextView = view.findViewById(R.id.TituloFormula)

        val botonHistorial = view.findViewById<ImageView>(R.id.verHistorial)

        botonHistorial.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("calculadoraId", calculadoraId!!)
            findNavController().navigate(R.id.navigation_historialCalculadora, bundle)
            Log.d("UsarCalculadoraFragment", "Navigating to HistorialCalculadoraFragment with id $calculadoraId")
        }
        // Recibir el calculadoraId
        calculadoraId = arguments?.getInt("calculadoraId")

        // Buscar la calculadora por ID
        calculadora = buscarCalculadoraPorId(calculadoraId)
        Tituloformula.text = calculadora?.formula

        if (calculadora != null) {
            val formula = calculadora!!.formula
            val variableNames = extraerVariablesDeFormula(formula)

            // Limpiar la lista de variables antes de agregar nuevas variables
            variables.clear()

            // Agregar variables a la lista
            for (variable in variableNames) {
                variables.add(Variable(variable, null))
            }

            // Configurar RecyclerView
            variablesRecyclerView.layoutManager = LinearLayoutManager(context)
            variablesRecyclerView.adapter = VariablesAdapter(variables)

            // Manejar el botón de calcular
            calculateButton.setOnClickListener {
                val values = variables.map { it.value }
                if (values.all { it != null }) {
                    // Realizar el cálculo con los valores ingresados
                    val result = calculate(formula, variableNames, values.filterNotNull())
                    // Guardar en el historial
                    val historial = HistorialCalculadora(
                        formula = formula,
                        idCalculadora = calculadoraId!!,
                        resultado = result,
                        variables = variables.toList()
                    )
                    HistorialCalculadora.addHistorial(historial)
                    // Mostrar el resultado (puedes usar un TextView o un Toast)
                    Toast.makeText(context, "Resultado: $result", Toast.LENGTH_LONG).show()
                } else {
                    // Manejar el caso donde uno o más valores no son válidos
                    Toast.makeText(
                        context,
                        "Por favor, ingrese todos los valores correctamente.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Toast.makeText(context, "Calculadora no encontrada.", Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun buscarCalculadoraPorId(id: Int?): DataCalculadoras? {
        return calculadorasList.find { it.id == id }
    }

    private fun extraerVariablesDeFormula(formula: String): List<String> {
        val regex = Regex("\\b[a-zA-Z]+\\b")
        val variables = regex.findAll(formula).map { it.value }.toList()
        return if (variables.size > 1) variables.drop(1) else emptyList()
    }

    private fun calculate(formula: String, variables: List<String>, values: List<Double>): Double {
        val variableMap = variables.zip(values).toMap()
        var parsedFormula = formula

        // Remove the left-hand side of the equation if present
        if (parsedFormula.contains("=")) {
            parsedFormula = parsedFormula.split("=").last().trim()
        }

        // Build the expression
        val expressionBuilder = ExpressionBuilder(parsedFormula)
        for ((variable, value) in variableMap) {
            expressionBuilder.variable(variable)
        }
        val expression = expressionBuilder.build()

        // Set variable values
        for ((variable, value) in variableMap) {
            expression.setVariable(variable, value)
        }

        // Evaluate the expression
        return try {
            expression.evaluate()
        } catch (e: Exception) {
            0.0
        }
    }
}

class VariablesAdapter(private val variables: MutableList<Variable>) : RecyclerView.Adapter<VariablesAdapter.VariableViewHolder>() {

    class VariableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val variableName: TextView = view.findViewById(R.id.variableName)
        val variableValue: EditText = view.findViewById(R.id.variableValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_variable, parent, false)
        return VariableViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariableViewHolder, position: Int) {
        val variable = variables[position]
        holder.variableName.text = variable.name
        holder.variableValue.hint = "Ingrese valor para ${variable.name}"
        holder.variableValue.setText(variable.value?.toString() ?: "")
        holder.variableValue.addTextChangedListener {
            variable.value = it.toString().toDoubleOrNull()
        }
    }

    override fun getItemCount(): Int = variables.size
}