package com.example.proyecto_de_titulo.ui.Cuestionarios

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.MainActivity
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.dataApiRest.PreguntaApi
import com.example.proyecto_de_titulo.dataApiRest.RespuestaApi
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class IntentarCuestionario : AppCompatActivity() {
    private lateinit var preguntaTextView: TextView
    private lateinit var botonSiguiente: Button
    private lateinit var botonSalir: Button
    private lateinit var recyclerView: RecyclerView
    private var preguntaActual = 0
    private var preguntas: List<PreguntaApi> = emptyList()
    private var respuestas: List<RespuestaApi> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intentar_cuestionario)

        preguntaTextView = findViewById(R.id.preguntaCuestionario)
        botonSiguiente = findViewById(R.id.botonSiguientePregunta)
        botonSalir = findViewById(R.id.botonSalir)
        recyclerView = findViewById(R.id.listadoRespuestas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val idCuestionario = intent.getStringExtra("idCuestionario") ?: return

        obtenerPreguntasYRespuestas(idCuestionario)

        botonSiguiente.setOnClickListener {
            if (preguntaActual < preguntas.size - 1) {
                preguntaActual++
                mostrarPregunta()
            }
        }

        botonSalir.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun obtenerPreguntasYRespuestas(idCuestionario: String) {
        val preguntaApiService = RetrofitClient.preguntaApiService
        val respuestaApiService = RetrofitClient.respuestaApiService

        preguntaApiService.getPreguntasByCuestionario(idCuestionario).enqueue(object : Callback<List<PreguntaApi>> {
            override fun onResponse(call: Call<List<PreguntaApi>>, response: Response<List<PreguntaApi>>) {
                if (response.isSuccessful) {
                    preguntas = response.body() ?: emptyList()
                    mostrarPregunta()
                } else {
                    Toast.makeText(this@IntentarCuestionario, "Failed to load questions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PreguntaApi>>, t: Throwable) {
                Toast.makeText(this@IntentarCuestionario, "Error loading questions", Toast.LENGTH_SHORT).show()
            }
        })

        respuestaApiService.getRespuestas().enqueue(object : Callback<List<RespuestaApi>> {
            override fun onResponse(call: Call<List<RespuestaApi>>, response: Response<List<RespuestaApi>>) {
                if (response.isSuccessful) {
                    respuestas = response.body() ?: emptyList()
                } else {
                    Toast.makeText(this@IntentarCuestionario, "Failed to load answers", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RespuestaApi>>, t: Throwable) {
                Toast.makeText(this@IntentarCuestionario, "Error loading answers", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarPregunta() {
        if (preguntas.isNotEmpty()) {
            val pregunta = preguntas[preguntaActual]
            preguntaTextView.text = pregunta.pregunta
            val respuestasFiltradas = respuestas.filter { it.idpregunta == pregunta.id }
            recyclerView.adapter = RespuestasAdapter(respuestasFiltradas)
        }
    }
}

class RespuestasAdapter(private val respuestas: List<RespuestaApi>) : RecyclerView.Adapter<RespuestasAdapter.ViewHolder>() {
    private var selectedPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val respuesta: RadioButton = view.findViewById(R.id.alternativa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tarjeta_alternativas_cuestionario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.respuesta.text = respuestas[position].respuesta
        holder.respuesta.isChecked = position == selectedPosition

        holder.respuesta.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = respuestas.size
}
