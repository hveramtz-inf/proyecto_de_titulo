package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

data class ProgresoCursoApi(
    val id: UUID,
    val idestudiante: UUID,
    val idcurso: UUID,
    val progreso: Float
)