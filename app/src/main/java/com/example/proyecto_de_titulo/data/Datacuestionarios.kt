package com.example.proyecto_de_titulo.data

data class DataSeccionCuestionarios(
    val id: Int,
    val titulo: String,
    val lista_cuestionarios: List<Datacuestionarios>,
)

data class Datacuestionarios(
    val id: Int,
    val titulo: String,
    val lista_preguntas: List<DataPreguntas>,
    var isFavorite: Boolean = false,
)

data class DataPreguntas(
    val id: Int,
    val pregunta: String,
    val tipo: String,
    val lista_respuestas: List<DataRespuestas>,
)

data class DataRespuestas(
    val id: Int,
    val respuesta: String,
    val correcta: Boolean,
)

// Ejemplo 1
val cuestionario1 = Datacuestionarios(
    id = 1,
    titulo = "Cuestionario de Geografía",
    lista_preguntas = listOf(
        DataPreguntas(
            id = 1,
            pregunta = "¿Cuál es la capital de España?",
            tipo = "Opción Múltiple",
            lista_respuestas = listOf(
                DataRespuestas(id = 1, respuesta = "Madrid", correcta = true),
                DataRespuestas(id = 2, respuesta = "Barcelona", correcta = false),
                DataRespuestas(id = 3, respuesta = "Valencia", correcta = false)
            )
        ),
        DataPreguntas(
            id = 2,
            pregunta = "¿Cuál es el río más largo del mundo?",
            tipo = "Opción Múltiple",
            lista_respuestas = listOf(
                DataRespuestas(id = 4, respuesta = "Amazonas", correcta = true),
                DataRespuestas(id = 5, respuesta = "Nilo", correcta = false),
                DataRespuestas(id = 6, respuesta = "Yangtsé", correcta = false)
            )
        )
    ),
    isFavorite = false
)

// Ejemplo 2
val cuestionario2 = Datacuestionarios(
    id = 2,
    titulo = "Cuestionario de Matemáticas",
    lista_preguntas = listOf(
        DataPreguntas(
            id = 3,
            pregunta = "¿Cuál es el resultado de 5 + 3?",
            tipo = "Opción Múltiple",
            lista_respuestas = listOf(
                DataRespuestas(id = 7, respuesta = "8", correcta = true),
                DataRespuestas(id = 8, respuesta = "7", correcta = false),
                DataRespuestas(id = 9, respuesta = "9", correcta = false)
            )
        ),
        DataPreguntas(
            id = 4,
            pregunta = "¿Cuál es el valor de pi?",
            tipo = "Opción Múltiple",
            lista_respuestas = listOf(
                DataRespuestas(id = 10, respuesta = "3.14", correcta = true),
                DataRespuestas(id = 11, respuesta = "3.15", correcta = false),
                DataRespuestas(id = 12, respuesta = "3.16", correcta = false)
            )
        )
    ),
    isFavorite = false
)

// Ejemplo 3
val cuestionario3 = Datacuestionarios(
    id = 3,
    titulo = "Cuestionario de Historia",
    lista_preguntas = listOf(
        DataPreguntas(
            id = 5,
            pregunta = "¿En qué año comenzó la Segunda Guerra Mundial?",
            tipo = "Opción Múltiple",
            lista_respuestas = listOf(
                DataRespuestas(id = 13, respuesta = "1939", correcta = true),
                DataRespuestas(id = 14, respuesta = "1940", correcta = false),
                DataRespuestas(id = 15, respuesta = "1941", correcta = false)
            )
        ),
        DataPreguntas(
            id = 6,
            pregunta = "¿Quién fue el primer presidente de los Estados Unidos?",
            tipo = "Opción Múltiple",
            lista_respuestas = listOf(
                DataRespuestas(id = 16, respuesta = "George Washington", correcta = true),
                DataRespuestas(id = 17, respuesta = "Thomas Jefferson", correcta = false),
                DataRespuestas(id = 18, respuesta = "Abraham Lincoln", correcta = false)
            )
        )
    ),
    isFavorite = false
)

val seccionCuestionarios1 = DataSeccionCuestionarios(
    id = 1,
    titulo = "Sección de Geografía",
    lista_cuestionarios = listOf(cuestionario1)
)

val seccionCuestionarios2 = DataSeccionCuestionarios(
    id = 2,
    titulo = "Sección de Matemáticas",
    lista_cuestionarios = listOf(cuestionario2)
)

val seccionCuestionarios3 = DataSeccionCuestionarios(
    id = 3,
    titulo = "Sección de Historia",
    lista_cuestionarios = listOf(cuestionario3)
)