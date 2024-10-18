package com.example.proyecto_de_titulo.ui.Apuntes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.Apuntes
import com.example.proyecto_de_titulo.databinding.FragmentApuntesBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import jp.wasabeef.richeditor.RichEditor

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