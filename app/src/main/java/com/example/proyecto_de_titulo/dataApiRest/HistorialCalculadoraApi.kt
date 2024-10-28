package com.example.proyecto_de_titulo.dataApiRest

import java.util.Date
import java.util.UUID

data class HistorialCalculadoraApi(
    val id : UUID,
    val idcalculadora: UUID,
    val idestudiante: UUID,
    val formulalatex: String,
    val resultado: Float,
    val created_at: Date
)

