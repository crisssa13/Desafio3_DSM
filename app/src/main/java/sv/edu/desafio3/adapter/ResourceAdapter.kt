package sv.edu.desafio3.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sv.edu.desafio3.R
import sv.edu.desafio3.model.Resource
import sv.edu.desafio3.network.ApiService

class ResourceAdapter(
    private val context: Context,
    private var resourceList: MutableList<Resource>,
    private val apiService: ApiService,
    private val onEditResource: (Resource) -> Unit
) : RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder>() {

    class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txtTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.txtDescription)
        val typeTextView: TextView = itemView.findViewById(R.id.txtType)
        val linkTextView: TextView = itemView.findViewById(R.id.txtLink)
        val imageView: ImageView = itemView.findViewById(R.id.imgResource)
        val editButton: ImageView = itemView.findViewById(R.id.btnEdit)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resource, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource = resourceList[position]

        holder.titleTextView.text = resource.title


        holder.descriptionTextView.text = if (resource.description.length > 60) {
            resource.description.take(60) + "…"
        } else {
            resource.description
        }

        holder.typeTextView.text = "Tipo: ${resource.type}"
        holder.linkTextView.text = resource.url

        // Cargar las imagenes
        Picasso.get()
            .load(resource.imageUrl)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.imageView)

        // Abrir los enlace
        holder.linkTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resource.url))
            context.startActivity(intent)
        }

        // Edicion
        holder.editButton.setOnClickListener {
            onEditResource(resource)
        }

        // Eliminar recursos
        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Eliminar recurso")
                .setMessage("¿Estás seguro que deseas eliminar este recurso?")
                .setPositiveButton("Sí") { _, _ ->
                    val idInt = try {
                        resource.id.toInt()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "ID inválido", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    apiService.deleteRecurso(idInt).enqueue(object : retrofit2.Callback<Void> {
                        override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Recurso eliminado", Toast.LENGTH_SHORT).show()
                                resourceList.removeAt(holder.adapterPosition)
                                notifyItemRemoved(holder.adapterPosition)
                                notifyItemRangeChanged(holder.adapterPosition, resourceList.size)
                            } else {
                                Toast.makeText(context, "Error al eliminar recurso", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = resourceList.size

    // Actualizar lista
    fun updateList(newList: List<Resource>) {
        resourceList = newList.toMutableList()
        notifyDataSetChanged()
    }

    // buscar por título o tipo
    fun filter(query: String) {
        val filteredList = resourceList.filter {
            it.title.contains(query, ignoreCase = true) || it.type.contains(query, ignoreCase = true)
        }
        resourceList = filteredList.toMutableList()
        notifyDataSetChanged()
    }

    // Ordenar por título o tipo
    fun sortByTitle() {
        resourceList.sortBy { it.title }
        notifyDataSetChanged()
    }

    fun sortByType() {
        resourceList.sortBy { it.type }
        notifyDataSetChanged()
    }
}

