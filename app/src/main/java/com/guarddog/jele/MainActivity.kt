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
    private lateinit var overlayShop: View
    private lateinit var btnStart: Button
    private lateinit var btnRestart: Button
    private lateinit var btnShop: Button
    private lateinit var btnShopClose: Button
    private lateinit var btnSkin0: Button
    private lateinit var btnSkin1: Button
    private lateinit var btnSkin2: Button
    private lateinit var tvFinalScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView = findViewById(R.id.glView)
        tvCoins = findViewById(R.id.tvCoins)
        tvDistance = findViewById(R.id.tvDistance)

        overlayStart = findViewById(R.id.overlayStart)
        overlayGameOver = findViewById(R.id.overlayGameOver)
        overlayShop = findViewById(R.id.overlayShop)
        btnStart = findViewById(R.id.btnStart)
        btnRestart = findViewById(R.id.btnRestart)
        btnShop = findViewById(R.id.btnShop)
        btnShopClose = findViewById(R.id.btnShopClose)
        btnSkin0 = findViewById(R.id.btnSkin0)
        btnSkin1 = findViewById(R.id.btnSkin1)
        btnSkin2 = findViewById(R.id.btnSkin2)
        tvFinalScore = findViewById(R.id.tvFinalScore)

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

        btnShop.setOnClickListener {
            overlayShop.visibility = View.VISIBLE
        }

        btnShopClose.setOnClickListener {
            overlayShop.visibility = View.GONE
        }

        btnSkin0.setOnClickListener {
            if (glView.renderer?.buySkin(0) == true) overlayShop.visibility = View.GONE
        }
        btnSkin1.setOnClickListener {
            if (glView.renderer?.buySkin(1) == true) overlayShop.visibility = View.GONE
        }
        btnSkin2.setOnClickListener {
            if (glView.renderer?.buySkin(2) == true) overlayShop.visibility = View.GONE
        }

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    // Convenience properties for the bottom buttons to avoid multiple findViewById calls
    private val btnLeft by lazy { findViewById<Button>(R.id.btnLeft) }
    private val btnRight by lazy { findViewById<Button>(R.id.btnRight) }
    private val btnJump by lazy { findViewById<Button>(R.id.btnJump) }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }
}
