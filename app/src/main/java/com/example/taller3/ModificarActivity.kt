package com.example.taller3

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ModificarInformacionActivity : BarraActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var nombreEditText: TextInputEditText
    private lateinit var correoEditText: TextInputEditText
    private lateinit var apellidoEditText: TextInputEditText
    private lateinit var IdEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var guardarCambiosButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_informacion)
        setupToolbar()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        nombreEditText = findViewById(R.id.nombreEditText)
        correoEditText = findViewById(R.id.correoEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        IdEditText = findViewById(R.id.IdEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        guardarCambiosButton = findViewById(R.id.guardarCambiosButton)

        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            database.child("users").child(uid).get().addOnSuccessListener { dataSnapshot ->
                val name = dataSnapshot.child("name").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                val apellido = dataSnapshot.child("lastName").value.toString()
                val id = dataSnapshot.child("id").value.toString()

                nombreEditText.setText(name)
                correoEditText.setText(email)
                apellidoEditText.setText(apellido)
                IdEditText.setText(id)
            }
        }

        guardarCambiosButton.setOnClickListener {
            guardarCambios()
        }
    }

    private fun guardarCambios() {
        val nombre = nombreEditText.text.toString().trim()
        val correo = correoEditText.text.toString().trim()
        val apellido = apellidoEditText.text.toString().trim()
        val id = IdEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (nombre.isEmpty() || correo.isEmpty() || apellido.isEmpty() || id.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isNotEmpty() && password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            val userMap = hashMapOf<String, Any>(
                "name" to nombre,
                "email" to correo,
                "lastName" to apellido,
                "id" to id
            )

            database.child("users").child(uid).updateChildren(userMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (password.isNotEmpty()) {
                        currentUser.updatePassword(password).addOnCompleteListener { passwordTask ->
                            if (passwordTask.isSuccessful) {
                                Toast.makeText(this, "Información y contraseña actualizadas correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Información actualizada correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error al actualizar la información", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
