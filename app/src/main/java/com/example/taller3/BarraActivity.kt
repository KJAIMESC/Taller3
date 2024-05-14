package com.example.taller3

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class BarraActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    protected fun setupToolbar() {
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            showNavigationMenu(it)
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.salir_perfil -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.more -> {
                    val intent = Intent(this, ModificarActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.set_as_available -> {
                    setAvailabilityStatus(true)
                    true
                }
                R.id.set_as_unavailable -> {
                    setAvailabilityStatus(false)
                    true
                }
                else -> false
            }
        }
    }

    private fun showNavigationMenu(view: android.view.View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.navigation_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.set_as_available -> {
                    setAvailabilityStatus(true)
                    true
                }
                R.id.set_as_unavailable -> {
                    setAvailabilityStatus(false)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setAvailabilityStatus(isAvailable: Boolean) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = currentUser.uid
            val status = if (isAvailable) "disponible" else "no disponible"
            database.child("users").child(uid).child("estado").setValue(status).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val message = if (isAvailable) "Estás disponible" else "No estás disponible"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar el estado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
