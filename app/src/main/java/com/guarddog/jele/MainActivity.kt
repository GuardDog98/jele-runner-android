package com.guarddog.jele

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var glView: GameGLSurfaceView
    private lateinit var tvCoins: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView = findViewById(R.id.glView)
        tvCoins = findViewById(R.id.tvCoins)

        val btnLeft = findViewById<Button>(R.id.btnLeft)
        val btnRight = findViewById<Button>(R.id.btnRight)
        val btnJump = findViewById<Button>(R.id.btnJump)

        glView.renderer?.bindHud(tvCoins)

        btnLeft.setOnClickListener { glView.renderer?.onMoveLeft() }
        btnRight.setOnClickListener { glView.renderer?.onMoveRight() }
        btnJump.setOnClickListener { glView.renderer?.onJump() }

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }
}
