package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

import android.os.Parcel
import android.os.Parcelable

data class PuntajeAlumnoCuestionario(
    val id: UUID,
    val idestudiante: UUID,
    val idcuestionario: UUID,
    val puntaje: Float,
)




data class PreguntayRespuestaSeleccionadaEstudiante(
    val pregunta: String,
    val respuesta: String,
    val valorRespuesta: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pregunta)
        parcel.writeString(respuesta)
        parcel.writeByte(if (valorRespuesta) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PreguntayRespuestaSeleccionadaEstudiante> {
        override fun createFromParcel(parcel: Parcel): PreguntayRespuestaSeleccionadaEstudiante {
            return PreguntayRespuestaSeleccionadaEstudiante(parcel)
        }

        override fun newArray(size: Int): Array<PreguntayRespuestaSeleccionadaEstudiante?> {
            return arrayOfNulls(size)
        }
    }
}