// CalculadoraApi.kt
package com.example.proyecto_de_titulo.dataApiRest

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class CalculadoraApi(
    val id: UUID,
    val nombre: String?,
    val formula: String?,
    val latexformula: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        UUID.fromString(parcel.readString()),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id.toString())
        parcel.writeString(nombre)
        parcel.writeString(formula)
        parcel.writeString(latexformula)
    }

    override fun describeContents(): Int = 0

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (nombre?.hashCode() ?: 0)
        result = 31 * result + (formula?.hashCode() ?: 0)
        result = 31 * result + (latexformula?.hashCode() ?: 0)
        return result
    }

    companion object CREATOR : Parcelable.Creator<CalculadoraApi> {
        override fun createFromParcel(parcel: Parcel): CalculadoraApi {
            return CalculadoraApi(parcel)
        }

        override fun newArray(size: Int): Array<CalculadoraApi?> {
            return arrayOfNulls(size)
        }
    }
}