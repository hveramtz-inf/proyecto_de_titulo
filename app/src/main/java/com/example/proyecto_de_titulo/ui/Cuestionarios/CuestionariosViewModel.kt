package com.example.proyecto_de_titulo.ui.Cuestionarios

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyecto_de_titulo.data.DataSeccionCuestionarios
import com.example.proyecto_de_titulo.data.Datacuestionarios
import com.example.proyecto_de_titulo.data.cuestionario1
import com.example.proyecto_de_titulo.data.cuestionario2
import com.example.proyecto_de_titulo.data.cuestionario3

class CuestionariosViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _cuestionarios = MutableLiveData<List<DataSeccionCuestionarios>>().apply {
        value = listOf(
            DataSeccionCuestionarios(
                id = 1,
                titulo = "Cuestionario de Geografía",
                lista_cuestionarios = listOf(cuestionario1)
            ),
            DataSeccionCuestionarios(
                id = 2,
                titulo = "Cuestionario de Matemáticas",
                lista_cuestionarios = listOf(cuestionario2)
            ),
            DataSeccionCuestionarios(
                id = 3,
                titulo = "Cuestionario de Historia",
                lista_cuestionarios = listOf(cuestionario3)
            )
        )
    }

    val cuestionarios: LiveData<List<DataSeccionCuestionarios>> = _cuestionarios
}