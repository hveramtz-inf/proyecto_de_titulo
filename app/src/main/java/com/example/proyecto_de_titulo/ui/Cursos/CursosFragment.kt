package com.example.proyecto_de_titulo.ui.Cursos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.databinding.FragmentCursosBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentCursosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cursosViewModel = ViewModelProvider(this).get(CursosViewModel::class.java)

        _binding = FragmentCursosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obtener el array de títulos de los recursos
        val cursosTitulos = resources.getStringArray(R.array.cursos_titulos)

        // Crear el adaptador con la lista de títulos de cursos
        val adapter = CursosAdapter(cursosTitulos)

        // Configurar el RecyclerView con un LayoutManager y el adaptador
        binding.listadoCursos.layoutManager = LinearLayoutManager(context)
        binding.listadoCursos.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class CursosAdapter(private val cursosTitulos: Array<String>) :
    RecyclerView.Adapter<CursosAdapter.CursoViewHolder>() {

    class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cursoTitulo: TextView = itemView.findViewById(R.id.TituloCurso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_de_cursos, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        holder.cursoTitulo.text = cursosTitulos[position]
    }

    override fun getItemCount() = cursosTitulos.size
}