package com.example.taller3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var bSignUp: Button
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var bLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bSignUp = findViewById(R.id.bSignUp)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        bLogin = findViewById(R.id.bLogin)

        setup()
    }

    private fun setup(){
        title = "Iniciar Sesion"

        bSignUp.setOnClickListener {
            val registroIntent = Intent(this, RegistroActivity::class.java)
            startActivity(registroIntent)
        }

        bLogin.setOnClickListener {
            if (etEmail.text?.isNotEmpty() == true && etPassword.text?.isNotEmpty() == true) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val perfilIntent = Intent(this, PerfilActivity::class.java)
                        startActivity(perfilIntent)
                    } else {
                        showAlert("Contraseña o Correo incorrecto")
                    }
                }
            } else {
                showAlert("Por favor complete todos los campos")
            }
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
