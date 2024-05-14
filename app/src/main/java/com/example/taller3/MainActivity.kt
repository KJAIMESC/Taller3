package com.example.taller3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
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

        bSignUp.setOnClickListener(){
            val Registrointent = Intent(this, RegistroActivity::class.java)
            startActivity(Registrointent)
        }

        bLogin.setOnClickListener{
            if (etEmail.text?.isNotEmpty() == true && etPassword.text?.isNotEmpty() == true){

                FirebaseAuth.getInstance().signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString()).addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        val Perfilintent = Intent(this, PerfilActivity::class.java)
                        startActivity(Perfilintent)
                    } else {
                        showAlert()
                    }

                }

            }else{
                println("Por favor complete todos los campos")
            }
        }


    }

    private fun showAlert(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Contraseña o Correo incorrecto")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}