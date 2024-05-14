package com.example.taller3

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PerfilActivity : BarraActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var nombreTextView: TextView
    private lateinit var correoTextView: TextView
    private lateinit var latitudTextView: TextView
    private lateinit var longitudTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        nombreTextView = findViewById(R.id.nombre)
        correoTextView = findViewById(R.id.correo)
        latitudTextView = findViewById(R.id.latitud)
        longitudTextView = findViewById(R.id.longitud)

        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            database.child("users").child(uid).get().addOnSuccessListener { dataSnapshot ->
                val name = dataSnapshot.child("name").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                val latitud = dataSnapshot.child("latitud").value.toString()
                val longitud = dataSnapshot.child("longitud").value.toString()

                nombreTextView.text = name
                correoTextView.text = email
                latitudTextView.text = latitud
                longitudTextView.text = longitud
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener los datos del usuario.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
        }
    }
}

