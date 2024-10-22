package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

data class CuestionarioApi(
    val id : UUID,
    val Titulo : String,
    val idcurso : UUID
)


data class ResCuestionarioApi(
    val id : UUID,
    val Titulo : String,
    val idcurso : UUID
)

data class PreguntaApi(
    val id : UUID,
    val pregunta : String,
    val idcuestionario : UUID
)

data class ResPreguntasCuestionarioApi(
    val listaPreguntas : List<PreguntaApi>
)

data class RespuestaApi(
    val id : UUID,
    val respuesta : String,
    val valor : Boolean,
    val idpregunta : UUID
)

data class ResRespuestasPreguntaApi(
    val listaRespuestas : List<RespuestaApi>
)