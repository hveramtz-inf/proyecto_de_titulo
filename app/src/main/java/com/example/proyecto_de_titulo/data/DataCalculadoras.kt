package com.example.proyecto_de_titulo.data
import android.os.Parcel
import android.os.Parcelable

data class DataCalculadoras(
    val nombre: String,
    val formula: String,
    var favorito: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeString(formula)
        parcel.writeByte(if (favorito) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataCalculadoras> {
        override fun createFromParcel(parcel: Parcel): DataCalculadoras {
            return DataCalculadoras(parcel)
        }

        override fun newArray(size: Int): Array<DataCalculadoras?> {
            return arrayOfNulls(size)
        }
    }
}

// Ejemplos de DataCalculadoras
val calculadora1 = DataCalculadoras(
    nombre = "Calculadora de Área",
    formula = "A = l * w",
    favorito = true
)

val calculadora2 = DataCalculadoras(
    nombre = "Calculadora de Volumen",
    formula = "V = l * w * h",
    favorito = false
)

val calculadora3 = DataCalculadoras(
    nombre = "Calculadora de Velocidad",
    formula = "v = d / t",
    favorito = true
)

// Lista de ejemplos
val calculadorasList = listOf(calculadora1, calculadora2, calculadora3)