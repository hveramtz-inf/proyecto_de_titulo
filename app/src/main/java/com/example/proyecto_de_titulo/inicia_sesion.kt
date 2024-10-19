package com.example.proyecto_de_titulo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class inicia_sesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicia_sesion)

        val inputRut = findViewById<EditText>(R.id.inputRut)
        val inputContrasenia = findViewById<EditText>(R.id.inputContrasenia)
        val iniciarSesionBoton = findViewById<Button>(R.id.iniciarSesionBoton)
        inputRut.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false
            private var isDeleting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return

                isFormatting = true
                val formatted = formatRut(s.toString(), isDeleting)
                inputRut.setText(formatted)
                inputRut.setSelection(formatted.length)
                isFormatting = false
            }
        })
        iniciarSesionBoton.setOnClickListener {
            val rut = inputRut.text.toString()
            val contrasenia = inputContrasenia.text.toString()

            // Aquí deberías validar las credenciales antes de guardarlas
            if (validarCredenciales(rut, contrasenia)) {
                guardarCredenciales(rut, contrasenia)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun formatRut(rut: String, isDeleting: Boolean): String {
        val cleanRut = rut.replace("[^\\d]".toRegex(), "").take(10)
        val length = cleanRut.length

        if (length <= 1) return cleanRut

        val sb = StringBuilder(cleanRut)
        if (length > 1) sb.insert(length - 1, "-")
        if (length > 4) sb.insert(length - 4, ".")
        if (length > 7) sb.insert(length - 7, ".")
        if (length > 10) sb.insert(length - 10, "0")

        return sb.toString()
    }

    private fun validarCredenciales(rut: String, contrasenia: String): Boolean {
        // Implementa la lógica de validación aquí
        return true
    }

    private fun guardarCredenciales(rut: String, contrasenia: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("RUT", rut)
        editor.putString("Contrasenia", contrasenia)
        editor.putString("Nombre", "Nombre de usuario")
        editor.apply()
    }
}