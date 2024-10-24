package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

data class VariableHistorialApi(
    val id: UUID,
    val idhistorial: UUID,
    val variable: String,
    val valor: Number
)
