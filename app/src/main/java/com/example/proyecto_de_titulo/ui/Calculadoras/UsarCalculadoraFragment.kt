// UsarCalculadoraFragment.kt
package com.example.proyecto_de_titulo.ui.Calculadoras

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
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
import com.example.proyecto_de_titulo.data.Variable
import com.example.proyecto_de_titulo.dataApiRest.CalculadoraApi
import net.objecthunter.exp4j.ExpressionBuilder

class UsarCalculadoraFragment : Fragment() {

    private lateinit var variablesRecyclerView: RecyclerView
    private lateinit var calculateButton: Button
    private lateinit var tituloFormula: WebView
    private var calculadora: CalculadoraApi? = null
    private val variables = mutableListOf<Variable>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usar_calculadora, container, false)
        variablesRecyclerView = view.findViewById(R.id.listadoDeVariables)
        calculateButton = view.findViewById(R.id.botonCalcular)
        tituloFormula = view.findViewById(R.id.TituloFormula)
        val botonHistorial = view.findViewById<ImageView>(R.id.verHistorial)

        botonHistorial.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("calculadoraId", calculadora?.id.toString())
            findNavController().navigate(R.id.navigation_historialCalculadora, bundle)
        }

        // Recibir el calculadoraItem
        calculadora = arguments?.getParcelable("calculadoraItem")
        calculadora?.let { it.latexformula?.let { it1 -> renderLaTeX(it1) } }

        if (calculadora != null) {
            val formula = calculadora!!.formula
            val variableNames = formula?.let { extraerVariablesDeFormula(it) }

            // Limpiar la lista de variables antes de agregar nuevas variables
            variables.clear()

            // Agregar variables únicas a la lista
            if (variableNames != null) {
                for (variable in variableNames.distinct()) {
                    variables.add(Variable(variable, null))
                }
            }

            // Configurar RecyclerView
            variablesRecyclerView.layoutManager = LinearLayoutManager(context)
            variablesRecyclerView.adapter = VariablesAdapter(variables)

            // Manejar el botón de calcular
            calculateButton.setOnClickListener {
                val values = variables.map { it.value }
                if (values.all { it != null }) {
                    // Realizar el cálculo con los valores ingresados
                    val result =
                        formula?.let { it1 -> variableNames?.let { it2 -> calculate(it1, it2.distinct(), values.filterNotNull()) } }
                    // Guardar en el historial
                    /*val historial = HistorialCalculadora(
                        // Add necessary fields here
                    )
                    HistorialCalculadora.addHistorial(historial)
                    // Mostrar el resultado (puedes usar un TextView o un Toast)*/
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

    private fun renderLaTeX(latex: String) {
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
                    "HTML-CSS": { scale: 100, linebreaks: { automatic: true } },
                    SVG: { scale: 100, linebreaks: { automatic: true } }
                });
            </script>
        </head>
        <body style="display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0;">
            <div id="math-content" style="text-align: center;">
                $$ $latex $$
            </div>
        </body>
        </html>
        """.trimIndent()

        tituloFormula.settings.javaScriptEnabled = true
        tituloFormula.settings.loadWithOverviewMode = true
        tituloFormula.settings.useWideViewPort = true
        tituloFormula.loadDataWithBaseURL(null, mathJaxConfig, "text/html", "utf-8", null)
}

    private fun extraerVariablesDeFormula(formula: String): List<String> {
        val regex = Regex("\\b[a-zA-Z_]+\\b")
        val rightHandSide = formula.split("=").last().trim()
        return regex.findAll(rightHandSide).map { it.value }.distinct().toList()
    }

    private fun calculate(formula: String, variables: List<String>, values: List<Double>): Double {
        val variableMap = variables.zip(values).toMap()
        var parsedFormula = formula

        // Remove the left-hand side of the equation if present
        if (parsedFormula.contains("=")) {
            parsedFormula = parsedFormula.split("=").last().trim()
        }

        // Replace ** with ^ for exponentiation
        parsedFormula = parsedFormula.replace("**", "^")

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