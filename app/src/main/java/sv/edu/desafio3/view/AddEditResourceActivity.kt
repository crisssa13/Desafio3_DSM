package sv.edu.desafio3.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sv.edu.desafio3.databinding.ActivityAddEditResourceBinding
import sv.edu.desafio3.model.Resource
import sv.edu.desafio3.network.ApiService

class AddEditResourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditResourceBinding
    private lateinit var apiService: ApiService
    private var resourceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditResourceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        apiService = Retrofit.Builder()
            .baseUrl("https://690620dbee3d0d14c134e9b4.mockapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)


        resourceId = intent.getStringExtra("RESOURCE_ID")
        if (resourceId != null) loadResource(resourceId!!)

        binding.btnSave.setOnClickListener { saveOrUpdateResource() }
    }

    private fun loadResource(id: String) {
        binding.progressBar.visibility = View.VISIBLE
        apiService.getRecursos().enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val resource = response.body()?.find { it.id == id }
                    if (resource != null) {
                        binding.etTitle.setText(resource.title)
                        binding.etDescription.setText(resource.description)
                        binding.etType.setText(resource.type)
                        binding.etUrl.setText(resource.url)
                        binding.etImageUrl.setText(resource.imageUrl)
                    }
                } else {
                    Toast.makeText(this@AddEditResourceActivity, "Error al cargar recurso", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AddEditResourceActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveOrUpdateResource() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val type = binding.etType.text.toString().trim()
        val url = binding.etUrl.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || type.isEmpty() || url.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val recurso = Resource(
            id = resourceId ?: "",
            title = title,
            description = description,
            type = type,
            url = url,
            imageUrl = imageUrl
        )

        binding.progressBar.visibility = View.VISIBLE

        val call: Call<Resource> = if (resourceId != null) {
            apiService.updateRecurso(resourceId!!.toInt(), recurso)
        } else {
            apiService.addRecurso(recurso)
        }

        call.enqueue(object : Callback<Resource> {
            override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@AddEditResourceActivity,
                        if (resourceId != null) "Recurso actualizado" else "Recurso agregado",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(this@AddEditResourceActivity, "Error al guardar recurso", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Resource>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@AddEditResourceActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
