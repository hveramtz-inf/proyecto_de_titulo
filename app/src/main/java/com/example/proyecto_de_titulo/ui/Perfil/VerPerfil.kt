package com.example.proyecto_de_titulo.ui.Perfil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyecto_de_titulo.R

class VerPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            val sharedPreferences = getSharedPreferences("Credenciales", MODE_PRIVATE)
            val nombre = sharedPreferences.getString("Nombre", "")
            val rut = sharedPreferences.getString("RUT", "")

            val tituloNombre = findViewById<TextView>(R.id.tituloNombre)
            val tituloRut = findViewById<TextView>(R.id.TituloRut)

            tituloNombre.text = nombre
            tituloRut.text = rut

            val botonVolverAlHome = findViewById<Button>(R.id.BotonVolverAlHome)
            botonVolverAlHome.setOnClickListener {
                finish()
            }

            val botonCerrarSesion = findViewById<Button>(R.id.botonCerrarSesion)
            botonCerrarSesion.setOnClickListener {
                val editor = sharedPreferences.edit()
                editor.remove("Nombre")
                editor.remove("RUT")
                editor.remove("Contrasenia")
                editor.apply()
                val intent = Intent(this, com.example.proyecto_de_titulo.MainActivity::class.java)
                startActivity(intent)
            }

            insets
        }
    }
}