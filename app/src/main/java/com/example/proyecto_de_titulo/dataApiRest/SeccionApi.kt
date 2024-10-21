package com.example.proyecto_de_titulo.dataApiRest

data class SeccionApi(
    val id: String, // UUID as String
    val idcurso: String, // UUID as String
    val titulo: String,
    val contenido: String,
    val linkvideoyoutube: String? // Nullable String
)