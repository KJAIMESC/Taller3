package com.example.taller3

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PerfilActivity : BarraActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var nombreTextView: TextView
    private lateinit var correoTextView: TextView
    private lateinit var latitudTextView: TextView
    private lateinit var longitudTextView: TextView
    private lateinit var estadoTextView: TextView
    private lateinit var availableUsersRecyclerView: RecyclerView
    private lateinit var availableUsersAdapter: AvailableUsersAdapter

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

        availableUsersRecyclerView.layoutManager = LinearLayoutManager(this)
        availableUsersAdapter = AvailableUsersAdapter(emptyList(), this)
        availableUsersRecyclerView.adapter = availableUsersAdapter

        // Check if user is authenticated
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            // Retrieve user data
            database.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("name").getValue(String::class.java) ?: ""
                    val email = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                    val latitud = dataSnapshot.child("latitud").getValue(Any::class.java)?.toString()?.toDoubleOrNull() ?: 0.0
                    val longitud = dataSnapshot.child("longitud").getValue(Any::class.java)?.toString()?.toDoubleOrNull() ?: 0.0
                    val estado = dataSnapshot.child("estado").getValue(String::class.java) ?: ""

                    nombreTextView.text = name
                    correoTextView.text = email
                    latitudTextView.text = latitud.toString()
                    longitudTextView.text = longitud.toString()
                    estadoTextView.text = estado

                    val color = if (estado == "Disponible") {
                        ContextCompat.getColor(this@PerfilActivity, android.R.color.holo_green_light)
                    } else {
                        ContextCompat.getColor(this@PerfilActivity, android.R.color.holo_red_light)
                    }
                    estadoTextView.setTextColor(color)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("PerfilActivity", "Error al obtener los datos del usuario.", databaseError.toException())
                    Toast.makeText(this@PerfilActivity, "Error al obtener los datos del usuario.", Toast.LENGTH_SHORT).show()
                }
            })

            // Retrieve list of available users
            database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val availableUsers = mutableListOf<User>()
                    for (snapshot in dataSnapshot.children) {
                        val userStatus = snapshot.child("estado").getValue(String::class.java)
                        if (userStatus == "Disponible") {
                            val userName = snapshot.child("name").getValue(String::class.java) ?: ""
                            val userEmail = snapshot.child("email").getValue(String::class.java) ?: ""
                            val userLatitud = snapshot.child("latitud").getValue(Any::class.java)?.toString()?.toDoubleOrNull() ?: 0.0
                            val userLongitud = snapshot.child("longitud").getValue(Any::class.java)?.toString()?.toDoubleOrNull() ?: 0.0

                            if (userName.isNotEmpty() && userEmail.isNotEmpty()) {
                                val userId = snapshot.key ?: ""
                                val user = User(userName, userEmail, userLatitud, userLongitud, userId)
                                availableUsers.add(user)
                                Log.d("PerfilActivity", "Usuario disponible encontrado: $userName - $userEmail")
                            }
                        }
                    }

                    // Log number of available users
                    Log.d("PerfilActivity", "Number of available users: ${availableUsers.size}")

                    // Set up adapter with the list of available users
                    availableUsersAdapter.updateUsers(availableUsers)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("PerfilActivity", "Error al obtener la lista de usuarios disponibles.", databaseError.toException())
                    Toast.makeText(this@PerfilActivity, "Error al obtener la lista de usuarios disponibles.", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.e("PerfilActivity", "Usuario no autenticado.")
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}
