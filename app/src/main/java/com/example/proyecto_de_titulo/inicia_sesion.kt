package com.example.proyecto_de_titulo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_de_titulo.dataApiRest.LoginRequest
import com.example.proyecto_de_titulo.dataApiRest.LoginResponse
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import android.widget.TextView

class inicia_sesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicia_sesion)

        val inputRut = findViewById<EditText>(R.id.inputRut)
        val inputContrasenia = findViewById<EditText>(R.id.inputContrasenia)
        val iniciarSesionBoton = findViewById<Button>(R.id.iniciarSesionBoton)
        val indicadorInicioSesion = findViewById<TextView>(R.id.indicadorInicioSesion)

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

            indicadorInicioSesion.text = "Cargando"
            indicadorInicioSesion.visibility = View.VISIBLE

            validarCredenciales(rut, contrasenia) { isValid ->
                if (isValid) {
                    indicadorInicioSesion.text = "Inicio correcto"
                    indicadorInicioSesion.setTextColor(Color.GREEN)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    indicadorInicioSesion.text = "Rut o contraseña erróneo"
                    indicadorInicioSesion.setTextColor(Color.RED)
                }
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

    private fun validarCredenciales(rut: String, contrasenia: String, callback: (Boolean) -> Unit) {
        val apiService = RetrofitClient.alumnoApiService
        val loginRequest = LoginRequest(rut, contrasenia)

        apiService.IniciarSesion(loginRequest).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        guardarCredenciales(loginResponse)
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback(false)
            }
        })
    }

    private fun guardarCredenciales(loginResponse: LoginResponse) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("RUT", loginResponse.rut)
        editor.putString("Contrasenia", loginResponse.contrasenia)
        editor.putString("Nombre", loginResponse.nombre)
        editor.putString("IdEstudiante", loginResponse.idestudiante?.toString() ?: "")
        editor.putString("ClavePucvid", loginResponse.clavepucv?.toString() ?: "")
        editor.apply()
    }
}