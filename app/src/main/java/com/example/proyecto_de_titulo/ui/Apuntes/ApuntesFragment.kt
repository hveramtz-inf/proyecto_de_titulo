package com.example.proyecto_de_titulo.ui.Apuntes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.Apuntes
import com.example.proyecto_de_titulo.databinding.FragmentApuntesBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ApuntesFragment : Fragment() {

    private var _binding: FragmentApuntesBinding? = null
    private val binding get() = _binding!!
    private var idSeccion: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idSeccion = it.getInt("seccionId")
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
            val apunte = Apuntes(id = 1, idSeccion = idSeccion ?: 0, apuntes = textoEditor)
            guardarApunte(apunte)
        }

        val botonEliminar = root.findViewById<Button>(R.id.EliminarApunte)
        botonEliminar.setOnClickListener {
            eliminarApunte()
        }

        cargarApunte()

        return root
    }

    private fun guardarApunte(apunte: Apuntes) {
        val sharedPreferences = requireContext().getSharedPreferences("ApuntesPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("apunte_${apunte.idSeccion}", apunte.apuntes)
        editor.apply()
    }

    private fun cargarApunte() {
        idSeccion?.let {
            val sharedPreferences = requireContext().getSharedPreferences("ApuntesPrefs", Context.MODE_PRIVATE)
            val apunte = sharedPreferences.getString("apunte_$it", "")
            if (!apunte.isNullOrEmpty()) {
                binding.editor.html = apunte
            }
        }
    }

    private fun eliminarApunte() {
        idSeccion?.let {
            val sharedPreferences = requireContext().getSharedPreferences("ApuntesPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("apunte_$it")
            editor.apply()
            binding.editor.html = ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}