package com.example.proyecto_de_titulo.ui.Seccion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.dataApiRest.SeccionApi
import com.example.proyecto_de_titulo.dataApiRest.SeccionRevisadaApi
import com.example.proyecto_de_titulo.interfazApiRest.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class SeccionesFragment : Fragment() {
    private var seccionrevisada = emptyList<SeccionRevisadaApi>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_secciones, container, false)

        val seccion = arguments?.getParcelable<SeccionApi>("seccion")

        val sharedPreferences = requireActivity().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)
        val estudianteId = sharedPreferences.getString("IdEstudiante", "0") ?: "0"

        obtenerSeccionesRevisadas(estudianteId, seccion?.id.toString())
        if (seccionrevisada.isEmpty()) {
            seccion?.id?.let { crearSeccionRevisada(estudianteId, it, seccion?.idcurso) }
        }

        val titulo = view.findViewById<TextView>(R.id.tituloSeccion)
        val contenido = view.findViewById<TextView>(R.id.contenidoSeccion)
        val linkyoutube = view.findViewById<WebView>(R.id.videoSeccion)
        val botonApuntes = view.findViewById<Button>(R.id.botonIrApunte)

        titulo.text = seccion?.titulo
        contenido.text = seccion?.contenido

        if (seccion?.linkvideoyoutube == null) {
            linkyoutube.visibility = View.INVISIBLE
        } else {
            linkyoutube.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url ?: "")
                    return true
                }
            }
            linkyoutube.webChromeClient = WebChromeClient()
            linkyoutube.settings.javaScriptEnabled = true
            linkyoutube.settings.domStorageEnabled = true
            linkyoutube.settings.mediaPlaybackRequiresUserGesture = false

            // Convert the YouTube URL to embed format
            val videoUrl = convertToEmbedUrl(seccion.linkvideoyoutube)
            linkyoutube.loadUrl(videoUrl)
        }

        botonApuntes.setOnClickListener {
            val bundle = bundleOf("seccionId" to seccion?.id.toString())
            findNavController().navigate(R.id.navigation_apuntes, bundle)
        }
        return view
    }

    private fun obtenerSeccionesRevisadas(idEstudiante: String, idSeccion: String) {
        RetrofitClient.seccionRevisadaApiService.getSeccionRevisadaByEstudianteAndSeccion(idEstudiante, idSeccion)
            .enqueue(object : Callback<List<SeccionRevisadaApi>> {
                override fun onResponse(call: Call<List<SeccionRevisadaApi>>, response: Response<List<SeccionRevisadaApi>>) {
                    if (response.isSuccessful) {
                        seccionrevisada = response.body() ?: emptyList()
                        // Handle the retrieved secciones revisadas here
                    } else {
                        Log.e("SeccionesFragment", "Failed to fetch secciones revisadas: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<List<SeccionRevisadaApi>>, t: Throwable) {
                    Log.e("SeccionesFragment", "Error fetching secciones revisadas", t)
                }
            })
    }

    private fun crearSeccionRevisada(idEstudiante: String, idSeccion: UUID, idCurso: String?) {
        val seccionRevisada = SeccionRevisadaApi(UUID.randomUUID(), idEstudiante, idSeccion, idCurso)
        RetrofitClient.seccionRevisadaApiService.createSeccionRevisada(seccionRevisada)
            .enqueue(object : Callback<SeccionRevisadaApi> {
                override fun onResponse(call: Call<SeccionRevisadaApi>, response: Response<SeccionRevisadaApi>) {
                    if (response.isSuccessful) {
                        val createdSeccionRevisada = response.body()
                        if (createdSeccionRevisada != null) {
                            seccionrevisada = listOf(createdSeccionRevisada)
                            // Handle the created seccion revisada here
                        }
                    } else {
                        Log.e("SeccionesFragment", "Failed to create seccion revisada: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<SeccionRevisadaApi>, t: Throwable) {
                    Log.e("SeccionesFragment", "Error creating seccion revisada", t)
                }
            })
    }

    private fun convertToEmbedUrl(youtubeUrl: String): String {
        return when {
            youtubeUrl.contains("youtube.com/shorts/") -> {
                val videoId = youtubeUrl.split("youtube.com/shorts/")[1].split("?")[0]
                "https://www.youtube.com/embed/$videoId?rel=0"
            }
            youtubeUrl.contains("v=") -> {
                val videoId = youtubeUrl.split("v=")[1].split("&")[0]
                "https://www.youtube.com/embed/$videoId?rel=0"
            }
            else -> {
                // Handle other cases or return a default embed URL
                "https://www.youtube.com/embed/"
            }
        }
    }
}