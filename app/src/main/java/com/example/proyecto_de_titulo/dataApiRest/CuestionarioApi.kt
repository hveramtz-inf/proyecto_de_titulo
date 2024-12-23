package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID
import android.os.Parcel
import android.os.Parcelable

data class CuestionarioApi(
    val id: UUID,
    val titulo: String,
    val idcurso: UUID,
    val ocultar : Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        UUID.fromString(parcel.readString()),
        parcel.readString() ?: "",
        UUID.fromString(parcel.readString()),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id.toString())
        parcel.writeString(titulo)
        parcel.writeString(idcurso.toString())
        parcel.writeByte(if (ocultar) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CuestionarioApi> {
        override fun createFromParcel(parcel: Parcel): CuestionarioApi {
            return CuestionarioApi(parcel)
        }

        override fun newArray(size: Int): Array<CuestionarioApi?> {
            return arrayOfNulls(size)
        }
    }
}

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