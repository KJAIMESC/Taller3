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

class AvailableUsersAdapter(private var users: List<User>, private val context: Context) : RecyclerView.Adapter<AvailableUsersAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userTextView: TextView = view.findViewById(R.id.userTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.userTextView.text = "${user.name} - ${user.email}"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MapaActivity::class.java).apply {
                putExtra("availableUserLat", user.latitud)
                putExtra("availableUserLng", user.longitud)
                putExtra("availableUserName", user.name)
                putExtra("availableUserId", user.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}

