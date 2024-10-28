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

    companion object CREATOR : Parcelable.Creator<CalculadoraApi> {
        override fun createFromParcel(parcel: Parcel): CalculadoraApi {
            return CalculadoraApi(parcel)
        }

        override fun newArray(size: Int): Array<CalculadoraApi?> {
            return arrayOfNulls(size)
        }
    }
}

// Examples of CalculadoraApi
val calculadora1 = CalculadoraApi(
    id = UUID.randomUUID(),
    nombre = "Calculadora de Área",
    formula = "A = l * w",
    latexformula = null
)

val calculadora2 = CalculadoraApi(
    id = UUID.randomUUID(),
    nombre = "Calculadora de Volumen",
    formula = "V = l * w * h",
    latexformula = null
)

val calculadora3 = CalculadoraApi(
    id = UUID.randomUUID(),
    nombre = "Calculadora de Velocidad",
    formula = "v = d / t",
    latexformula = null
)

// List of examples
val calculadorasList = listOf(calculadora1, calculadora2, calculadora3)