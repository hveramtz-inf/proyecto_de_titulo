package com.example.proyecto_de_titulo.interfazApiRest

import com.example.proyecto_de_titulo.dataApiRest.CursoApi
import com.example.proyecto_de_titulo.dataApiRest.SeccionApi
import retrofit2.Call
import retrofit2.http.GET

interface CursoApiService {
    @GET("cursos")
    fun getCursos(): Call<List<CursoApi>>
}

interface SeccionApiService {
    @GET("secciones")
    fun getSecciones(): Call<List<SeccionApi>>
}