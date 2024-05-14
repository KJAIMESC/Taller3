package com.example.taller3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ModificarInformacionActivity : BarraActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var nombreEditText: TextInputEditText
    private lateinit var correoEditText: TextInputEditText
    private lateinit var latitudEditText: TextInputEditText
    private lateinit var longitudEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar)
        setupToolbar()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        nombreEditText = findViewById(R.id.nombreEditText)
        correoEditText = findViewById(R.id.correoEditText)
        latitudEditText = findViewById(R.id.latitudEditText)
        longitudEditText = findViewById(R.id.longitudEditText)

        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            database.child("users").child(uid).get().addOnSuccessListener { dataSnapshot ->
                val name = dataSnapshot.child("name").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                val latitud = dataSnapshot.child("latitud").value.toString()
                val longitud = dataSnapshot.child("longitud").value.toString()

                nombreEditText.setText(name)
                correoEditText.setText(email)
                latitudEditText.setText(latitud)
                longitudEditText.setText(longitud)
            }
        }
    }
}
