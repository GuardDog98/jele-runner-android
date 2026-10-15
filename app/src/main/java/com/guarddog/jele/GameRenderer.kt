package com.guarddog.jele

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import kotlin.math.max

class GameRenderer(val context: Context) : GLSurfaceView.Renderer {

    private lateinit var player: Player
    private lateinit var ground: Ground

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vpMatrix = FloatArray(16)

    private var lastTime = System.nanoTime()

    // Camera
    private val cameraPosition = floatArrayOf(0f, 5.5f, -11f)
    private val cameraLookAt = floatArrayOf(0f, 0f, 10f)

    override fun onSurfaceCreated(unused: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0.11f, 0.17f, 0.28f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        player = Player()
        ground = Ground()
    }

    override fun onSurfaceChanged(unused: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.perspectiveM(projectionMatrix, 0, 60f, ratio, 0.1f, 200f)
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
        player.draw(vpMatrix)
    }

    private fun update(delta: Float) {
        // Move camera and look-at forward with player's progress
        player.update(delta)
        val camZ = player.position[2] - 11f
        cameraPosition[2] = camZ
        cameraLookAt[2] = player.position[2] + 6f

        // simple rotation animation for player
        player.rotationY += 120f * delta
    }

    // Input hooks from UI
    fun onMoveLeft() {
        player.moveLeft()
    }
    fun onMoveRight() { player.moveRight() }
    fun onJump() { player.jump() }
}
