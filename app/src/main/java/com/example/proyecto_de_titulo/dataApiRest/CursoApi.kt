package com.example.proyecto_de_titulo.dataApiRest

data class CursoApi(
    val id: String, // UUID as String
    val nombre: String,
    val descripcion: String?,
    val clavepucvid: String // UUID as String
)
