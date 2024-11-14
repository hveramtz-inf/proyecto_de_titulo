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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.Variable
import com.example.proyecto_de_titulo.dataApiRest.CalculadoraApi
import com.example.proyecto_de_titulo.dataApiRest.HistorialCalculadoraApi
import com.example.proyecto_de_titulo.dataApiRest.VariableHistorialApi
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.Date
import java.util.UUID

class UsarCalculadoraFragment : Fragment() {

    private lateinit var variablesRecyclerView: RecyclerView
    private lateinit var calculateButton: Button
    private lateinit var tituloFormula: WebView
    private lateinit var resultadoTextView: TextView
    private lateinit var textoFormulaConValores: TextView
    private lateinit var layoutTextoFormulayTextoResultado: LinearLayout
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
        resultadoTextView = view.findViewById(R.id.TextoResultado)
        textoFormulaConValores = view.findViewById(R.id.textoFormulaConValores)
        layoutTextoFormulayTextoResultado = view.findViewById(R.id.layoutTextoFormulayTextoResultado)
        val botonHistorial = view.findViewById<ImageView>(R.id.verHistorial)

        botonHistorial.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("calculadoraId", calculadora?.id.toString())
            Log.d("CalculadoraId", calculadora?.id.toString())
            findNavController().navigate(R.id.navigation_historialCalculadora, bundle)
        }

        calculadora = arguments?.getParcelable("calculadoraItem")
        calculadora?.let { it.latexformula?.let { it1 -> renderLaTeX(it1) } }

        if (calculadora != null) {
            val formula = calculadora!!.formula
            val variableNames = formula?.let { extraerVariablesDeFormula(it) }

            variables.clear()
            if (variableNames != null) {
                for (variable in variableNames.distinct()) {
                    variables.add(Variable(variable, null))
                }
            }

            variablesRecyclerView.layoutManager = LinearLayoutManager(context)
            variablesRecyclerView.adapter = VariablesAdapter(variables)

            calculateButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    val values = variables.map { it.value }
                    if (values.all { it != null }) {
                        val result = formula?.let { it1 -> variableNames?.let { it2 -> calculate(it1, it2.distinct(), values.filterNotNull()) } }

                        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
                        val idEstudiante = sharedPreferences.getString("IdEstudiante", null)?.let { UUID.fromString(it) }

                        if (idEstudiante != null && calculadora != null) {
                            val historialCalculadora = HistorialCalculadoraApi(
                                id = UUID.randomUUID(),
                                idcalculadora = calculadora!!.id,
                                idestudiante = idEstudiante,
                                formulalatex = calculadora!!.latexformula ?: "",
                                resultado = result?.toFloat() ?: 0.0f,
                                created_at = Date()
                            )
                            val historialId = createHistorialCalculadora(historialCalculadora)

                            if (historialId != null) {
                                variables.forEach { variable ->
                                    createVariableHistorial(variable, historialId)
                                }
                            }

                            val updatedFormula = formula?.let { it1 ->
                                if (variableNames != null) {
                                    replaceVariablesWithValues(it1, variableNames, values.filterNotNull())
                                } else {
                                    ""
                                }
                            }
                            textoFormulaConValores.text = updatedFormula
                            textoFormulaConValores.text = updatedFormula.toString()
                            resultadoTextView.text = "Resultado: $result"
                            layoutTextoFormulayTextoResultado.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(context, "Error: IdEstudiante or Calculadora is null.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Please fill in all variable values.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Calculadora no encontrada.", Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun replaceVariablesWithValues(formula: String, variables: List<String>, values: List<Double>): String {
        var updatedFormula = formula
        for ((variable, value) in variables.zip(values)) {
            updatedFormula = updatedFormula.replace(variable, value.toString())
        }
        return updatedFormula
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
        <body style="display:block; justify-content: center; align-items: center; height: 100vh; margin: 0;">
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
        val regex = Regex("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")
        val rightHandSide = formula.split("=").last().trim()
        return regex.findAll(rightHandSide).map { it.value }.distinct().toList()
    }

    private fun calculate(formula: String, variables: List<String>, values: List<Double>): Double {
        val variableMap = variables.zip(values).toMap()
        var parsedFormula = formula

        if (parsedFormula.contains("=")) {
            parsedFormula = parsedFormula.split("=").last().trim()
        }

        parsedFormula = parsedFormula.replace("**", "^")

        val expressionBuilder = ExpressionBuilder(parsedFormula)
        for ((variable, value) in variableMap) {
            expressionBuilder.variable(variable)
        }
        val expression = expressionBuilder.build()

        for ((variable, value) in variableMap) {
            expression.setVariable(variable, value)
        }

        return try {
            expression.evaluate()
        } catch (e: Exception) {
            0.0
        }
    }

    private suspend fun createHistorialCalculadora(historialCalculadora: HistorialCalculadoraApi): UUID? {
        return withContext(Dispatchers.IO) {
            val call = RetrofitClient.historialCalculadoraApiService.createHistorialCalculadora(historialCalculadora)
            val response = call.execute()
            if (response.isSuccessful) {
                response.body()?.id
            } else {
                Log.e("API Error", "Failed to create HistorialCalculadoraApi: ${response.errorBody()?.string()}")
                null
            }
        }
    }

    private suspend fun createVariableHistorial(variable: Variable, historialId: UUID) {
        val variableHistorial = VariableHistorialApi(
            id = UUID.randomUUID(),
            idhistorial = historialId,
            variable = variable.name,
            valor = variable.value!!
        )
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.variableHistorialApiService.createVariableHistorial(variableHistorial).execute()
        }
        if (!response.isSuccessful) {
            Log.e("API Error", "Failed to create VariableHistorialApi: ${response.errorBody()?.string()}")
        }
    }
}

class VariablesAdapter(private val variables: MutableList<Variable>) : RecyclerView.Adapter<VariablesAdapter.VariableViewHolder>() {

    class VariableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val variableName: TextView = view.findViewById(R.id.variableName)
        val variableValue: EditText = view.findViewById(R.id.variableValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_variable, parent, false)
        return VariableViewHolder(view)
    }

    override fun onBindViewHolder(holder: VariableViewHolder, position: Int) {
        val variable = variables[position]
        holder.variableName.text = variable.name
        holder.variableValue.setText(variable.value?.toString() ?: "")
        holder.variableValue.addTextChangedListener {
            variable.value = it.toString().toDoubleOrNull()
        }
    }

    override fun getItemCount(): Int = variables.size
}