package com.example.taller3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieListener

class MainActivity : AppCompatActivity() {

    private lateinit var animationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animationView = findViewById(R.id.animation_view)

        // Load LottieComposition from raw resource
        val compositionTask = LottieCompositionFactory.fromRawRes(this, R.raw.spylottie)
        compositionTask.addListener(object : LottieListener<LottieComposition> {
            override fun onResult(result: LottieComposition?) {
                result?.let {
                    animationView.setComposition(it)
                    animationView.playAnimation()
                }
            }
        })
    }
}

