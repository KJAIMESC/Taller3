package com.example.taller3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var bSignUp: Button
    private lateinit var etName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etId: TextInputEditText
    private lateinit var etLatitud: TextInputEditText
    private lateinit var etLongitud: TextInputEditText
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        bSignUp = findViewById(R.id.bSignUp)
        etName = findViewById(R.id.etName)
        etLastName = findViewById(R.id.etLastName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etId = findViewById(R.id.etId)
        etLatitud = findViewById(R.id.etLatitud)
        etLongitud = findViewById(R.id.etLongitud)

        // Solicitar permisos de ubicación si no están concedidos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getLastKnownLocation()
        }

        setUp()
    }

    private fun setUp() {
        title = "Registro"

        bSignUp.setOnClickListener {
            if (etName.text?.isNotEmpty() == true &&
                etLastName.text?.isNotEmpty() == true &&
                etEmail.text?.isNotEmpty() == true &&
                etPassword.text?.isNotEmpty() == true &&
                etId.text?.isNotEmpty() == true &&
                etLatitud.text?.isNotEmpty() == true &&
                etLongitud.text?.isNotEmpty() == true
            ) {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = auth.currentUser
                        user?.let {
                            val uid = user.uid
                            val latitud = etLatitud.text.toString().toDoubleOrNull()
                            val longitud = etLongitud.text.toString().toDoubleOrNull()

                            if (latitud == null || longitud == null) {
                                showAlert("Error al convertir latitud/longitud a Double.")
                                return@addOnCompleteListener
                            }

                            val userMap = hashMapOf(
                                "name" to etName.text.toString(),
                                "lastName" to etLastName.text.toString(),
                                "email" to email,
                                "id" to etId.text.toString(),
                                "latitud" to latitud,
                                "longitud" to longitud
                            )

                            database.child("users").child(uid).setValue(userMap).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val perfilIntent = Intent(this, PerfilActivity::class.java)
                                    startActivity(perfilIntent)
                                } else {
                                    showAlert("Error al guardar los datos del usuario: ${task.exception?.message}")
                                }
                            }
                        }
                    } else {
                        showAlert("Error autenticando al usuario: ${task.exception?.message}")
                    }
                }
            } else {
                showAlert("Por favor complete todos los campos.")
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

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                etLatitud.setText(location.latitude.toString())
                etLongitud.setText(location.longitude.toString())
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
