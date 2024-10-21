package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

data class ApuntesApi(
    val id : UUID,
    val Apunte: String,
    val idSeccion: UUID,
    val idAlumno: UUID
)
