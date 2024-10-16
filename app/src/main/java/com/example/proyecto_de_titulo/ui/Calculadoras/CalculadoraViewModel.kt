// CalculadoraViewModel.kt
package com.example.proyecto_de_titulo.ui.Calculadoras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculadoraViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Calculadora Fragment"
    }
    val text: LiveData<String> = _text
}