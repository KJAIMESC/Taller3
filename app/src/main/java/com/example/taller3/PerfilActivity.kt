package com.example.taller3

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        // Check if user is authenticated
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            // Retrieve user data
            database.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
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
                            val userName = snapshot.child("name").getValue(String::class.java)
                            val userEmail = snapshot.child("email").getValue(String::class.java)
                            val userLatitudStr = snapshot.child("latitud").getValue(String::class.java)
                            val userLongitudStr = snapshot.child("longitud").getValue(String::class.java)

                            val userLatitud = userLatitudStr?.toDoubleOrNull() ?: 0.0
                            val userLongitud = userLongitudStr?.toDoubleOrNull() ?: 0.0

                            if (!userName.isNullOrEmpty() && !userEmail.isNullOrEmpty()) {
                                val user = User(userName, userEmail, userLatitud, userLongitud)
                                availableUsers.add(user)
                                Log.d("PerfilActivity", "Usuario disponible encontrado: $userName - $userEmail")
                            }
                        }
                    }

                    // Log number of available users
                    Log.d("PerfilActivity", "Number of available users: ${availableUsers.size}")

                    // Initialize RecyclerView and set up adapter
                    availableUsersRecyclerView = findViewById(R.id.availableUsersRecyclerView)
                    availableUsersRecyclerView.layoutManager = LinearLayoutManager(this@PerfilActivity)
                    availableUsersAdapter = AvailableUsersAdapter(availableUsers, this@PerfilActivity)
                    availableUsersRecyclerView.adapter = availableUsersAdapter
                    availableUsersAdapter.notifyDataSetChanged() // Notify adapter of data change
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
