package com.example.proyecto_de_titulo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (credencialesGuardadas()) {
            // Navegar a la actividad principal
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish() // Finalizar MainActivity
        } else {
            // Navegar a la actividad de inicio de sesión
            val intent = Intent(this, inicia_sesion::class.java)
            startActivity(intent)
            finish() // Finalizar MainActivity
        }
    }

    private fun credencialesGuardadas(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val rut = sharedPreferences.getString("RUT", null)
        val contrasenia = sharedPreferences.getString("Contrasenia", null)
        return !rut.isNullOrEmpty() && !contrasenia.isNullOrEmpty()
    }
}