package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

data class AlumnoApi(
    val idestudiante: UUID,
    val nombre: String,
    val rut: String,
    val contrasenia: String,
    val clavepucv: UUID
)

data class LoginRequest(
    val rut: String,
    val contrasenia: String
)

data class LoginResponse(
    val idestudiante: UUID,
    val nombre: String,
    val rut: String,
    val contrasenia: String,
    val clavepucv: UUID
)