package com.example.taller3

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taller3.MapaActivity
import com.example.taller3.R
import com.example.taller3.User

class AvailableUsersAdapter(
    private val users: List<User>,
    private val context: Context
) : RecyclerView.Adapter<AvailableUsersAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.nombre_disponible)
        val correoTextView: TextView = view.findViewById(R.id.correo_disponible)
        val btnLocalizar: Button = view.findViewById(R.id.btn_disponible)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usuarios_disponibles, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        Log.d("AvailableUsersAdapter", "User: ${user.name}, ${user.email}")

        holder.nombreTextView.text = user.name
        holder.correoTextView.text = user.email

        holder.btnLocalizar.setOnClickListener {
            val user = users[position]

            // Log the data being passed as extras to MapaActivity
            Log.d("AvailableUsersAdapter", "Launching MapaActivity for user: ${user.name}")
            Log.d("AvailableUsersAdapter", "User details: name=${user.name}, email=${user.email}, latitud=${user.latitud}, longitud=${user.longitud}")

            // Create an intent to launch MapaActivity and pass necessary extras
            val intent = Intent(context, MapaActivity::class.java).apply {
                putExtra("availableUserLat", user.latitud)
                putExtra("availableUserLng", user.longitud)
                putExtra("availableUserName", user.name)
            }

            // Start the MapaActivity with the intent
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }
}
