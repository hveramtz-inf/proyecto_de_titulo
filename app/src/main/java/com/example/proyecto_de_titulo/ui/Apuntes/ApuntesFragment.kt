package com.example.proyecto_de_titulo.ui.Apuntes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.Apuntes
import com.example.proyecto_de_titulo.dataApiRest.ApuntesApi
import com.example.proyecto_de_titulo.dataApiRest.ReqCreateApunteApi
import com.example.proyecto_de_titulo.dataApiRest.reqUpdateApunteApi
import com.example.proyecto_de_titulo.databinding.FragmentApuntesBinding
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class ApuntesFragment : Fragment() {

    private var _binding: FragmentApuntesBinding? = null
    private val binding get() = _binding!!
    private var idSeccion: String? = null
    private var currentApunte: ApuntesApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idSeccion = it.getString("seccionId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApuntesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        binding.editor.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bottomNav?.visibility = View.GONE
            } else {
                bottomNav?.visibility = View.VISIBLE
            }
        }

        val botonGuardar = root.findViewById<Button>(R.id.GuardarApunte)
        botonGuardar.setOnClickListener {
        val textoEditor = binding.editor.html
        val idEstudiante = getIdEstudiante(requireContext())
        if (idEstudiante != null && idSeccion != null) {
            if (currentApunte != null) {
                val apunte = reqUpdateApunteApi(currentApunte!!.id.toString(), textoEditor)
                actualizarApunte(apunte)
            } else {
                val apunte = ReqCreateApunteApi(
                    idseccion = UUID.fromString(idSeccion),
                    idestudiante = UUID.fromString(idEstudiante),
                    apunte = textoEditor
                )
                crearApunte(apunte)
            }
        } else {
            Toast.makeText(context, "Missing idEstudiante or idSeccion", Toast.LENGTH_SHORT).show()
        }
    }

        val botonEliminar = root.findViewById<Button>(R.id.EliminarApunte)
        botonEliminar.setOnClickListener {
            eliminarApunte()
        }

        // Set up action buttons
        // State variables for action buttons
        var isBoldActive = false
        var isItalicActive = false
        var isUnderlineActive = false
        var isBulletsActive = false
        var isNumbersActive = false

        // Set up action buttons
        root.findViewById<Button>(R.id.action_bold).setOnClickListener {
            isBoldActive = !isBoldActive
            binding.editor.setBold()
            Toast.makeText(context, if (isBoldActive) "Bold activated" else "Bold deactivated", Toast.LENGTH_SHORT).show()
        }

        root.findViewById<Button>(R.id.action_italic).setOnClickListener {
            isItalicActive = !isItalicActive
            binding.editor.setItalic()
            Toast.makeText(context, if (isItalicActive) "Italic activated" else "Italic deactivated", Toast.LENGTH_SHORT).show()
        }

        root.findViewById<Button>(R.id.action_underline).setOnClickListener {
            isUnderlineActive = !isUnderlineActive
            binding.editor.setUnderline()
            Toast.makeText(context, if (isUnderlineActive) "Underline activated" else "Underline deactivated", Toast.LENGTH_SHORT).show()
        }

        root.findViewById<Button>(R.id.action_bullets).setOnClickListener {
            isBulletsActive = !isBulletsActive
            binding.editor.setBullets()
            Toast.makeText(context, if (isBulletsActive) "Bullets activated" else "Bullets deactivated", Toast.LENGTH_SHORT).show()
        }

        root.findViewById<Button>(R.id.action_numbers).setOnClickListener {
            isNumbersActive = !isNumbersActive
            binding.editor.setNumbers()
            Toast.makeText(context, if (isNumbersActive) "Numbers activated" else "Numbers deactivated", Toast.LENGTH_SHORT).show()
        }

        cargarApunte()

        // Retrieve and log idEstudiante
        val idEstudiante = getIdEstudiante(requireContext())
        Toast.makeText(context, "IdEstudiante: $idEstudiante", Toast.LENGTH_SHORT).show()

        return root
    }

    private fun guardarApunte(apunte: Apuntes) {
        val sharedPreferences = requireContext().getSharedPreferences("ApuntesPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("apunte_${apunte.idSeccion}", apunte.apuntes)
        editor.apply()
    }

    private fun cargarApunte() {
        val idEstudiante = getIdEstudiante(requireContext())
        val idSeccion = idSeccion

        if (idEstudiante != null && idSeccion != null) {
            val apiService = RetrofitClient.apuntesApiService
            apiService.getApunte(idEstudiante, idSeccion).enqueue(object : Callback<ApuntesApi> {
                override fun onResponse(call: Call<ApuntesApi>, response: Response<ApuntesApi>) {
                    if (response.isSuccessful) {
                        val apunte = response.body()
                        if (apunte != null) {
                            currentApunte = apunte
                            binding.editor.html = apunte.apunte
                        }
                    } else {
                        Toast.makeText(context, "Failed to load apunte", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApuntesApi>, t: Throwable) {
                    Toast.makeText(context, "Error loading apunte", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Missing idEstudiante or idSeccion", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarApunte() {
        currentApunte?.let {
            val apiService = RetrofitClient.apuntesApiService
            apiService.deleteApunte(it.id.toString()).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        binding.editor.html = ""
                        currentApunte = null
                        Toast.makeText(context, "Apunte eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to delete apunte", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "Error deleting apunte", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun actualizarApunte(apunte: reqUpdateApunteApi) {
        val apiService = RetrofitClient.apuntesApiService
        apiService.updateApunte(apunte.idapunte, apunte).enqueue(object : Callback<ApuntesApi> {
            override fun onResponse(call: Call<ApuntesApi>, response: Response<ApuntesApi>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Apunte actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update apunte", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApuntesApi>, t: Throwable) {
                Toast.makeText(context, "Error updating apunte", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun crearApunte(apunte: ReqCreateApunteApi) {
        val apiService = RetrofitClient.apuntesApiService
        apiService.createApunte(apunte).enqueue(object : Callback<ApuntesApi> {
            override fun onResponse(call: Call<ApuntesApi>, response: Response<ApuntesApi>) {
                if (response.isSuccessful) {
                    currentApunte = response.body()
                    Toast.makeText(context, "Apunte creado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to create apunte", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApuntesApi>, t: Throwable) {
                Toast.makeText(context, "Error creating apunte", Toast.LENGTH_SHORT).show()
            }
        })
    }

private fun getIdEstudiante(context: Context): String? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
    return sharedPreferences.getString("IdEstudiante", null)
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}