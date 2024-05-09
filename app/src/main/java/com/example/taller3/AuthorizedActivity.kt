package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.taller3.utils.Alerts
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

open class AuthorizedActivity : AppCompatActivity() {
    private var auth: FirebaseAuth = Firebase.auth
    private var currentUser = auth.currentUser
    var alerts = Alerts(this)

    override fun onStart() {
        super.onStart()
        if(auth.currentUser == null){
            logout()
        }
    }

    override fun onResume() {
        super.onResume()
        if(auth.currentUser == null){
            logout()
        }
    }

    protected fun logout(){
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}