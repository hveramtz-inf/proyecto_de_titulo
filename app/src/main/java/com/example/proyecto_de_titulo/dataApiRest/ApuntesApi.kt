package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

data class ApuntesApi(
    val id: UUID,
    val apunte: String,
    val idseccion: UUID,
    val idestudiante: UUID
)

data class ReqApuntesApi(
    val idseccion: UUID,
    val idestudiante: UUID
)

data class ReqCreateApunteApi(
    val idseccion: UUID,
    val idestudiante: UUID,
    val apunte: String
)

data class ReqDeleteApunteApi(
    val idapunte: UUID
)

data class reqUpdateApunteApi(
    val idapunte: String,
    val apunte: String
)





