package com.guarddog.jele

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.widget.TextView
import java.util.Random
import kotlin.math.abs

class GameRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var player: Player
    private lateinit var ground: Ground
    private val coins = mutableListOf<Coin>()
    private val random = Random()

    private var coinValue = 0
    private var hudTextView: TextView? = null

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vpMatrix = FloatArray(16)
    private var lastTime = System.nanoTime()

    private val cameraPosition = floatArrayOf(0f, 5.5f, -11f)
    private val cameraLookAt = floatArrayOf(0f, 0f, 10f)

    fun bindHud(textView: TextView) {
        hudTextView = textView
        updateHud()
    }

    override fun onSurfaceCreated(unused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0.08f, 0.14f, 0.22f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        player = Player()
        ground = Ground()
        spawnCoins()
    }

    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.perspectiveM(projectionMatrix, 0, 62f, ratio, 0.1f, 200f)
    }

    override fun onDrawFrame(unused: javax.microedition.khronos.opengles.GL10?) {
        val now = System.nanoTime()
        val delta = (now - lastTime) / 1_000_000_000f
        lastTime = now

        update(delta)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, cameraPosition[0], cameraPosition[1], cameraPosition[2], cameraLookAt[0], cameraLookAt[1], cameraLookAt[2], 0f, 1f, 0f)
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        ground.draw(vpMatrix)
        coins.filter { it.isAlive }.forEach { it.draw(vpMatrix) }
        player.draw(vpMatrix)
    }

    private fun update(delta: Float) {
        player.update(delta)

        for (coin in coins) {
            if (!coin.isAlive) continue
            coin.update(delta)

            val dx = abs(coin.position[0] - player.position[0])
            val dy = abs(coin.position[1] - player.position[1])
            val dz = abs(coin.position[2] - player.position[2])

            if (dx < 0.9f && dy < 1.2f && dz < 1.2f) {
                coin.isAlive = false
                coinValue += 1
                updateHud()
            }
        }

        // Spawn a new coin line when player moves forward enough.
        val farthestNeeded = player.position[2] + 60f
        if (coins.none { it.position[2] > farthestNeeded && it.isAlive } && coins.count { it.isAlive } < 12) {
            spawnCoins()
        }

        val camZ = player.position[2] - 11f
        cameraPosition[2] = camZ
        cameraLookAt[2] = player.position[2] + 6f
        player.rotationY += 110f * delta
    }

    private fun spawnCoins() {
        for (i in 0..11) {
            val lane = random.nextInt(3) - 1
            val x = lane * 3.2f
            val z = player.position[2] + 20f + i * 11f
            val coin = Coin(x, 1.2f, z)
            coins.add(coin)
        }
    }

    private fun updateHud() {
        val text = "Coins: $coinValue"
        hudTextView?.post {
            hudTextView?.text = text
        }
    }

    fun onMoveLeft() { player.moveLeft() }
    fun onMoveRight() { player.moveRight() }
    fun onJump() { player.jump() }
}
