package com.example.proyecto_de_titulo.dataApiRest

import java.util.UUID

import android.os.Parcel
import android.os.Parcelable

data class FavoritosCalculadora(
    val id: UUID = UUID.randomUUID(),
    val idestudiante: UUID,
    val idcalculadora: UUID
) : Parcelable {
    constructor(parcel: Parcel) : this(
        UUID.fromString(parcel.readString()),
        UUID.fromString(parcel.readString()),
        UUID.fromString(parcel.readString())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id.toString())
        parcel.writeString(idestudiante.toString())
        parcel.writeString(idcalculadora.toString())
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FavoritosCalculadora> {
        override fun createFromParcel(parcel: Parcel): FavoritosCalculadora {
            return FavoritosCalculadora(parcel)
        }

        override fun newArray(size: Int): Array<FavoritosCalculadora?> {
            return arrayOfNulls(size)
        }
    }
}