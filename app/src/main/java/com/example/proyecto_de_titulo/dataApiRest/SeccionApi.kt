package com.example.proyecto_de_titulo.dataApiRest

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class SeccionApi(
    val id: UUID, // Change id to UUID
    val titulo: String,
    val contenido: String,
    val linkvideoyoutube: String?,
    val idcurso: String? // Ensure idcurso is a String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        UUID.fromString(parcel.readString()), // Read UUID from Parcel
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id.toString()) // Write UUID as String to Parcel
        parcel.writeString(titulo)
        parcel.writeString(contenido)
        parcel.writeString(linkvideoyoutube)
        parcel.writeString(idcurso)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SeccionApi> {
        override fun createFromParcel(parcel: Parcel): SeccionApi {
            return SeccionApi(parcel)
        }

        override fun newArray(size: Int): Array<SeccionApi?> {
            return arrayOfNulls(size)
        }
    }
}