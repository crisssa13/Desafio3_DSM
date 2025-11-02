package sv.edu.desafio3

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import sv.edu.desafio3.adapter.ResourceAdapter
import sv.edu.desafio3.controller.AuthController
import sv.edu.desafio3.databinding.ActivityMainBinding
import sv.edu.desafio3.model.Resource
import sv.edu.desafio3.network.ApiService
import sv.edu.desafio3.view.AddEditResourceActivity
import sv.edu.desafio3.view.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authController: AuthController
    private lateinit var apiService: ApiService
    private lateinit var adapter: ResourceAdapter

    private val addResourceLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { fetchRecursos() }

    private val editResourceLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { fetchRecursos() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authController = AuthController(this)


        binding.recyclerViewRecursos.layoutManager = LinearLayoutManager(this)


        apiService = Retrofit.Builder()
            .baseUrl("https://690620dbee3d0d14c134e9b4.mockapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        adapter = ResourceAdapter(this, mutableListOf(), apiService) { resource ->
            val intent = Intent(this, AddEditResourceActivity::class.java)
            intent.putExtra("RESOURCE_ID", resource.id)
            editResourceLauncher.launch(intent)
        }

        binding.recyclerViewRecursos.adapter = adapter


        binding.progressBar.visibility = android.view.View.VISIBLE
        fetchRecursos()

        // Agregar recurso
        binding.btnAddResource.setOnClickListener {
            val intent = Intent(this, AddEditResourceActivity::class.java)
            addResourceLauncher.launch(intent)
        }

        // Cerrar sesión
        binding.btnLogout.setOnClickListener {
            authController.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Búsqueda
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { adapter.filter(it) }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { adapter.filter(it) }
                return false
            }
        })

        // Ordenar
        binding.btnSortTitle.setOnClickListener { adapter.sortByTitle() }
        binding.btnSortType.setOnClickListener { adapter.sortByType() }
    }

    private fun fetchRecursos() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        apiService.getRecursos().enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                binding.progressBar.visibility = android.view.View.GONE
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val recursos = response.body()!!.toMutableList()
                    adapter.updateList(recursos)
                } else {
                    adapter.updateList(mutableListOf())
                    Toast.makeText(this@MainActivity, "No hay recursos disponibles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                binding.progressBar.visibility = android.view.View.GONE
                Toast.makeText(this@MainActivity, "Error al conectar con la API: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
