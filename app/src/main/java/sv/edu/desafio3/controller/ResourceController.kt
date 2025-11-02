package sv.edu.desafio3.controller

import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sv.edu.desafio3.model.Resource
import sv.edu.desafio3.network.ApiClient

class ResourceController(private val context: Context) {

    fun loadResources(onResult: (List<Resource>) -> Unit) {
        ApiClient.service.getRecursos().enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) onResult(response.body() ?: emptyList())
                else Toast.makeText(context, "Error al obtener recursos", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
