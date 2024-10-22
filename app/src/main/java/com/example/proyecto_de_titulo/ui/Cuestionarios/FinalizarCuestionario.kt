package com.example.proyecto_de_titulo.ui.Cuestionarios

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_de_titulo.Home
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

        if (idCuestionario != null && idEstudiante != null) {
            obtenerPuntajeAlumnoCuestionario(idEstudiante, idCuestionario, porcentajeRespuestasCorrectas)
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
                        if (porcentajeRespuestasCorrectas > puntajeExistente.puntaje) {
                            actualizarPuntaje(puntajeExistente.id.toString(), porcentajeRespuestasCorrectas)
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

    private fun actualizarPuntaje(idPuntaje: String, nuevoPuntaje: Float) {
        val apiService = RetrofitClient.puntajeAlumnoCuestionarioApiService
        val puntajeActualizado = PuntajeAlumnoCuestionario(UUID.fromString(idPuntaje), UUID.randomUUID(), UUID.randomUUID(), nuevoPuntaje)

        apiService.updatePuntaje(idPuntaje, puntajeActualizado).enqueue(object : Callback<PuntajeAlumnoCuestionario> {
            override fun onResponse(call: Call<PuntajeAlumnoCuestionario>, response: Response<PuntajeAlumnoCuestionario>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FinalizarCuestionario, "Score updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@FinalizarCuestionario, Home::class.java)
                    intent.putExtra("destination", R.id.navigation_dashboard)
                    startActivity(intent)
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
        val nuevoPuntajeAlumnoCuestionario = PuntajeAlumnoCuestionario(UUID.randomUUID(), UUID.fromString(idEstudiante), UUID.fromString(idCuestionario), nuevoPuntaje)

        apiService.createPuntaje(nuevoPuntajeAlumnoCuestionario).enqueue(object : Callback<PuntajeAlumnoCuestionario> {
            override fun onResponse(call: Call<PuntajeAlumnoCuestionario>, response: Response<PuntajeAlumnoCuestionario>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FinalizarCuestionario, "Score created successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@FinalizarCuestionario, Home::class.java)
                    intent.putExtra("destination", R.id.navigation_dashboard)
                    startActivity(intent)
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