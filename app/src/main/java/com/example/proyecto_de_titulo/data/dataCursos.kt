package com.example.proyecto_de_titulo.data

import android.os.Parcel
import android.os.Parcelable

data class dataCursos(
    val id: Int,
    val string: String,
    val lista_secciones: List<DataSeccionCursos>,
)

data class DataSeccionCursos(
    val id: Int,
    val titulo: String,
    val contenido: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(titulo)
        parcel.writeString(contenido)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataSeccionCursos> {
        override fun createFromParcel(parcel: Parcel): DataSeccionCursos {
            return DataSeccionCursos(parcel)
        }

        override fun newArray(size: Int): Array<DataSeccionCursos?> {
            return arrayOfNulls(size)
        }
    }
}

// Example data for dataCursos
val curso1 = dataCursos(
    id = 1,
    string = "Curso de Programación",
    lista_secciones = listOf(
        DataSeccionCursos(
            id = 1,
            titulo = "Introducción a Kotlin",
            contenido = "Contenido de la introducción a Kotlin."
        ),
        DataSeccionCursos(
            id = 2,
            titulo = "Funciones en Kotlin",
            contenido = "Contenido sobre funciones en Kotlin."
        )
    )
)

val curso2 = dataCursos(
    id = 2,
    string = "Curso de Matemáticas",
    lista_secciones = listOf(
        DataSeccionCursos(
            id = 3,
            titulo = "Álgebra Básica",
            contenido = "Contenido de álgebra básica."
        ),
        DataSeccionCursos(
            id = 4,
            titulo = "Geometría",
            contenido = "Contenido sobre geometría."
        )
    )
)

// List of example courses
val listaCursos = listOf(curso1, curso2)