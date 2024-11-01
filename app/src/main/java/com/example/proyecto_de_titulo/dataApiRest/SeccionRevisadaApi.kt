package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

data class SeccionRevisadaApi(
    val id: UUID,
    val idestudiante: String,
    val idseccion: UUID,
    val idcurso: String?
)