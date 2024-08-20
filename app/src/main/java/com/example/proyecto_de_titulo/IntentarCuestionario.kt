package com.example.proyecto_de_titulo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.data.DataRespuestas
import com.example.proyecto_de_titulo.data.Datacuestionarios


// In the IntentarCuestionario activity
class IntentarCuestionario : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intentar_cuestionario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val idCuestionario = intent.getIntExtra("idCuestionario", -1)
        val Cuestionario = Datacuestionarios.getCuestionarioID(idCuestionario) as Datacuestionarios
        val pregunta = findViewById<TextView>(R.id.preguntaCuestionario)
        var preguntaActual = 0
        val botonSiguiente = findViewById<Button>(R.id.botonSiguientePregunta)
        val botonSalir = findViewById<Button>(R.id.botonSalir)
        pregunta.text = Cuestionario.lista_preguntas[preguntaActual].pregunta


        val listaRespuestas = Cuestionario.lista_preguntas[preguntaActual].lista_respuestas
        Log.d("IntentarCuestionario", "Lista de respuestas: $listaRespuestas")

        botonSiguiente.setOnClickListener {
            if (preguntaActual < Cuestionario.lista_preguntas.size - 1) {
                preguntaActual++
                pregunta.text = Cuestionario.lista_preguntas[preguntaActual].pregunta
                val recyclerView = findViewById<RecyclerView>(R.id.listadoRespuestas)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = RespuestasAdapter(Cuestionario.lista_preguntas[preguntaActual].lista_respuestas)
            }
        }

        botonSalir.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.listadoRespuestas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RespuestasAdapter(Cuestionario.lista_preguntas[preguntaActual].lista_respuestas)
    }
}


class RespuestasAdapter(private val respuestas: List<DataRespuestas>) : RecyclerView.Adapter<RespuestasAdapter.ViewHolder>() {
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