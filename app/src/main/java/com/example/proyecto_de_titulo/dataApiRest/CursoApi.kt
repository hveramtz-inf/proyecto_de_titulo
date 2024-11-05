package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

data class CursoApi(
    val id: UUID, // Change id to UUID
    val nombre: String,
    val descripcion: String?,
    val clavepucvid: UUID, // Change clavepucvid to UUID
    val ocultar: Boolean
)