package sv.edu.desafio3.network

import retrofit2.Call
import retrofit2.http.*
import sv.edu.desafio3.model.Resource

interface ApiService {

    // Obtiene los recursos
    @GET("recursos")
    fun getRecursos(): Call<List<Resource>>

    // Crear un recurso
    @POST("recursos")
    fun addRecurso(@Body recurso: Resource): Call<Resource>

    // Actualizar un recurso
    @PUT("recursos/{id}")
    fun updateRecurso(@Path("id") id: Int, @Body recurso: Resource): Call<Resource>

    // Eliminar un recurso por
    @DELETE("recursos/{id}")
    fun deleteRecurso(@Path("id") id: Int): Call<Void>
}
