package com.example.proyecto_de_titulo.ui.Seccion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.proyecto_de_titulo.R
import com.example.proyecto_de_titulo.data.listaCursos

class SeccionesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_secciones, container, false)

        val seccionId = arguments?.getInt("seccionId")
        val seccion = listaCursos.flatMap { it.lista_secciones }
            .find { it.id == seccionId }

        val titulo = view.findViewById<TextView>(R.id.tituloSeccion)
        val contenido = view.findViewById<TextView>(R.id.contenidoSeccion)
        val linkyoutube = view.findViewById<WebView>(R.id.videoSeccion)

        titulo.text = seccion?.titulo
        contenido.text = seccion?.contenido

        if (seccion?.linkyoutube == null)
        {
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
            val videoUrl = convertToEmbedUrl(seccion.linkyoutube)
            linkyoutube.loadUrl(videoUrl)
        }

        return view
    }

    private fun convertToEmbedUrl(youtubeUrl: String): String {
        val videoId = youtubeUrl.split("v=")[1]
        return "https://www.youtube.com/embed/$videoId?rel=0"
    }
}