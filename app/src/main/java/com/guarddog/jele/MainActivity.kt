package com.guarddog.jele

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var glView: GameGLSurfaceView
    private lateinit var tvCoins: TextView
    private lateinit var tvDistance: TextView

    private lateinit var overlayStart: View
    private lateinit var overlayGameOver: View
    private lateinit var btnStart: Button
    private lateinit var btnRestart: Button
    private lateinit var tvFinalScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView = findViewById(R.id.glView)
        tvCoins = findViewById(R.id.tvCoins)
        tvDistance = findViewById(R.id.tvDistance)

        overlayStart = findViewById(R.id.overlayStart)
        overlayGameOver = findViewById(R.id.overlayGameOver)
        btnStart = findViewById(R.id.btnStart)
        btnRestart = findViewById(R.id.btnRestart)
        tvFinalScore = findViewById(R.id.tvFinalScore)

        val btnLeft = findViewById<Button>(R.id.btnLeft)
        val btnRight = findViewById<Button>(R.id.btnRight)
        val btnJump = findViewById<Button>(R.id.btnJump)

        glView.renderer?.bindHud(tvCoins, tvDistance)
        glView.renderer?.bindOverlays(overlayStart, overlayGameOver, tvFinalScore)

        btnLeft.setOnClickListener { glView.renderer?.onMoveLeft() }
        btnRight.setOnClickListener { glView.renderer?.onMoveRight() }
        btnJump.setOnClickListener { glView.renderer?.onJump() }

        btnStart.setOnClickListener {
            overlayStart.visibility = View.GONE
            glView.renderer?.startGame()
        }

        btnRestart.setOnClickListener {
            overlayGameOver.visibility = View.GONE
            glView.renderer?.restart()
        }

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
