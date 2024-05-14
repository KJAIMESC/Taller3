package com.example.taller3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue

class PerfilActivity : BarraActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var nombreTextView: TextView
    private lateinit var correoTextView: TextView
    private lateinit var latitudTextView: TextView
    private lateinit var longitudTextView: TextView
    private lateinit var estadoTextView: TextView
    private lateinit var availableUsersRecyclerView: RecyclerView
    private lateinit var mapButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        nombreTextView = findViewById(R.id.nombre)
        correoTextView = findViewById(R.id.correo)
        latitudTextView = findViewById(R.id.latitud)
        longitudTextView = findViewById(R.id.longitud)
        estadoTextView = findViewById(R.id.estado)
        availableUsersRecyclerView = findViewById(R.id.availableUsersRecyclerView)
        mapButton = findViewById(R.id.mapButton)

        availableUsersRecyclerView.layoutManager = LinearLayoutManager(this)

        mapButton.setOnClickListener {
            val intent = Intent(this, MapaActivity::class.java)
            startActivity(intent)
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            database.child("users").child(uid).get().addOnSuccessListener { dataSnapshot ->
                val name = dataSnapshot.child("name").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                val latitud = dataSnapshot.child("latitud").value.toString()
                val longitud = dataSnapshot.child("longitud").value.toString()
                val estado = dataSnapshot.child("estado").value.toString()

                nombreTextView.text = name
                correoTextView.text = email
                latitudTextView.text = latitud
                longitudTextView.text = longitud
                estadoTextView.text = estado

                val color = if (estado == "disponible") {
                    ContextCompat.getColor(this, android.R.color.holo_green_light)
                } else {
                    ContextCompat.getColor(this, android.R.color.holo_red_light)
                }
                estadoTextView.setTextColor(color)
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener los datos del usuario.", Toast.LENGTH_SHORT).show()
            }

            // Obtener la lista de usuarios disponibles
            database.child("users").get().addOnSuccessListener { dataSnapshot ->
                val availableUsers = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val userStatus = snapshot.child("estado").getValue<String>()
                    if (userStatus == "disponible") {
                        val userName = snapshot.child("name").getValue<String>()
                        val userEmail = snapshot.child("email").getValue<String>()
                        if (!userName.isNullOrEmpty() && !userEmail.isNullOrEmpty()) {
                            availableUsers.add("$userName - $userEmail")
                        }
                    }
                }
                availableUsersRecyclerView.adapter = AvailableUsersAdapter(availableUsers)
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener la lista de usuarios disponibles.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
