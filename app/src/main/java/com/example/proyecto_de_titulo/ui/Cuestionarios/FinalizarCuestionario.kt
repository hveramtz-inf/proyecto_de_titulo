package com.example.proyecto_de_titulo.ui.Cuestionarios

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.Home
import com.example.proyecto_de_titulo.MainActivity
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.dataApiRest.PreguntayRespuestaSeleccionadaEstudiante
import com.example.proyecto_de_titulo.dataApiRest.PuntajeAlumnoCuestionario
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID


class FinalizarCuestionario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalizar_cuestionario)

        val idCuestionario = intent.getStringExtra("idCuestionario")
        val preguntayrespuestas = intent.getParcelableArrayListExtra<PreguntayRespuestaSeleccionadaEstudiante>("respuestasSeleccionadas")
        val porcentajeRespuestasCorrectas = calcularPorcentajeRespuestasCorrectas(preguntayrespuestas?.toList() ?: emptyList())

        val sharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val idEstudiante = sharedPreferences.getString("IdEstudiante", null)

        // Set the text for respuestasCorrectas_de_total TextView
        val respuestasCorrectasTextView: TextView = findViewById(R.id.respuestasCorrectas_de_total)
        val numeroRespuestasCorrectas = preguntayrespuestas?.count { it.valorRespuesta } ?: 0
        val totalPreguntas = preguntayrespuestas?.size ?: 0
        respuestasCorrectasTextView.text = "$numeroRespuestasCorrectas de $totalPreguntas"

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.listado_pregunta_con_respuesta)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ListadoPreguntayRespuestaAdapter(preguntayrespuestas ?: emptyList())

        if (idCuestionario != null && idEstudiante != null) {
            obtenerPuntajeAlumnoCuestionario(idEstudiante, idCuestionario, porcentajeRespuestasCorrectas)
        }

        val botonFinalizarRevisionCuestionario: Button = findViewById(R.id.botonFinalizarRevisionCuestionario)
        botonFinalizarRevisionCuestionario.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerPuntajeAlumnoCuestionario(idEstudiante: String, idCuestionario: String, porcentajeRespuestasCorrectas: Float) {
        val apiService = RetrofitClient.puntajeAlumnoCuestionarioApiService
        apiService.getPuntajesByEstudiante(idEstudiante).enqueue(object : Callback<List<PuntajeAlumnoCuestionario>> {
            override fun onResponse(call: Call<List<PuntajeAlumnoCuestionario>>, response: Response<List<PuntajeAlumnoCuestionario>>) {
                if (response.isSuccessful) {
                    val puntajes = response.body()
                    val puntajeExistente = puntajes?.find { it.idcuestionario.toString() == idCuestionario }

                    if (puntajeExistente != null) {
                        if (porcentajeRespuestasCorrectas > puntajeExistente.puntaje && puntajeExistente.puntaje.toInt() != 100) {
                            actualizarPuntaje(puntajeExistente.id.toString(), idCuestionario, porcentajeRespuestasCorrectas)
                        }
                    } else {
                        crearNuevoPuntaje(idEstudiante, idCuestionario, porcentajeRespuestasCorrectas)
                    }
                } else {
                    Toast.makeText(this@FinalizarCuestionario, "Error fetching scores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PuntajeAlumnoCuestionario>>, t: Throwable) {
                Toast.makeText(this@FinalizarCuestionario, "Error fetching scores", Toast.LENGTH_SHORT).show()
            }
        })
    }

        private fun actualizarPuntaje(idPuntaje: String, idCuestionario: String, nuevoPuntaje: Float) {
        val apiService = RetrofitClient.puntajeAlumnoCuestionarioApiService
        val puntajeActualizado = PuntajeAlumnoCuestionario(
            UUID.fromString(idPuntaje),
            UUID.randomUUID(), // Asumiendo que este es el id del estudiante
            UUID.fromString(idCuestionario),
            nuevoPuntaje
        )

        apiService.updatePuntaje(idPuntaje, puntajeActualizado).enqueue(object : Callback<PuntajeAlumnoCuestionario> {
            override fun onResponse(call: Call<PuntajeAlumnoCuestionario>, response: Response<PuntajeAlumnoCuestionario>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FinalizarCuestionario, "Score updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FinalizarCuestionario, "Error updating score", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PuntajeAlumnoCuestionario>, t: Throwable) {
                Toast.makeText(this@FinalizarCuestionario, "Error updating score", Toast.LENGTH_SHORT).show()
            }
        })
    }

        private fun crearNuevoPuntaje(idEstudiante: String, idCuestionario: String, nuevoPuntaje: Float) {
        val apiService = RetrofitClient.puntajeAlumnoCuestionarioApiService
        val nuevoPuntajeAlumnoCuestionario = PuntajeAlumnoCuestionario(
            UUID.randomUUID(),
            UUID.fromString(idEstudiante),
            UUID.fromString(idCuestionario),
            nuevoPuntaje
        )

        apiService.createPuntaje(nuevoPuntajeAlumnoCuestionario).enqueue(object : Callback<PuntajeAlumnoCuestionario> {
            override fun onResponse(call: Call<PuntajeAlumnoCuestionario>, response: Response<PuntajeAlumnoCuestionario>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FinalizarCuestionario, "Score created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FinalizarCuestionario, "Error creating score", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PuntajeAlumnoCuestionario>, t: Throwable) {
                Toast.makeText(this@FinalizarCuestionario, "Error creating score", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

fun calcularPorcentajeRespuestasCorrectas(preguntayrespuestas: List<PreguntayRespuestaSeleccionadaEstudiante>): Float {
    var respuestasCorrectas = 0

    for (preguntaRespuesta in preguntayrespuestas) {
        if (preguntaRespuesta.valorRespuesta) {
            respuestasCorrectas++
        }
    }

    return if (preguntayrespuestas.isNotEmpty()) {
        (respuestasCorrectas.toFloat() / preguntayrespuestas.size) * 100
    } else {
        0f
    }
}


class ListadoPreguntayRespuestaAdapter(
    preguntayrespuestas: List<PreguntayRespuestaSeleccionadaEstudiante>
) : RecyclerView.Adapter<ListadoPreguntayRespuestaAdapter.ViewHolder>() {

    private val respuestasIncorrectas = preguntayrespuestas.filter { !it.valorRespuesta }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloPregunta: TextView = itemView.findViewById(R.id.TituloPreguntaFinales)
        val tituloRespuesta: TextView = itemView.findViewById(R.id.TituloRepuestaFinales)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_pregunta_y_respuesta_final_cuestionario, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = respuestasIncorrectas[position]
        holder.tituloPregunta.text = item.pregunta
        holder.tituloRespuesta.text = item.respuesta
    }

    override fun getItemCount() = respuestasIncorrectas.size
}
