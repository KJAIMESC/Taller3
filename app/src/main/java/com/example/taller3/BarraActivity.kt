package com.example.taller3

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class BarraActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barra)

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            showNavigationMenu(it)
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.salir_perfil -> {
                    // Cuando se selecciona "Salir del perfil", inicia MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.perfil -> {
                    //nos ibamos a perfim
                    val intent = Intent(this,PerfilActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.more -> {
                    // Manejar más elementos en el menú desbordamiento
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
                    true
                }
                // Agrega más casos si hay más ítems
                else -> false
            }
        }
        popup.show()
    }
}

