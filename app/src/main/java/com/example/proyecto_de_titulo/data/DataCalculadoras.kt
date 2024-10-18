package com.example.proyecto_de_titulo.data
import android.os.Parcel
import android.os.Parcelable
import android.util.Log

data class DataCalculadoras(
    val id: Int,
    val nombre: String,
    val formula: String,
    var favorito: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt() ?: 0,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
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
    id = 1,
    nombre = "Calculadora de Área",
    formula = "A = l * w",
    favorito = true
)

val calculadora2 = DataCalculadoras(
    id = 2,
    nombre = "Calculadora de Volumen",
    formula = "V = l * w * h",
    favorito = false
)

val calculadora3 = DataCalculadoras(
    id = 3,
    nombre = "Calculadora de Velocidad",
    formula = "v = d / t",
    favorito = true
)

// Lista de ejemplos
val calculadorasList = listOf(calculadora1, calculadora2, calculadora3)

data class Variable(val name: String, var value: Double?)


data class HistorialCalculadora(
    val formula: String,
    val idCalculadora: Int,
    val resultado: Double,
    val variables: List<Variable>
) {
    companion object {
        private val historialMap = mutableMapOf<Int, MutableList<HistorialCalculadora>>()

        fun addHistorial(historial: HistorialCalculadora) {
            val historialList = historialMap.getOrPut(historial.idCalculadora) { mutableListOf() }
            if (historialList.size >= 5) {
                historialList.removeAt(0) // Remove the oldest entry if the list exceeds 5
            }
            historialList.add(historial)

            // Log the added historial
            Log.d("HistorialCalculadora", "Added historial: $historial")
        }

        fun getHistorial(idCalculadora: Int): List<HistorialCalculadora> {
            val historial = historialMap[idCalculadora]?.toList() ?: emptyList()
            // Log the retrieved historial
            Log.d("HistorialCalculadora", "Retrieved historial for id $idCalculadora: $historial")
            return historial
        }
    }
}