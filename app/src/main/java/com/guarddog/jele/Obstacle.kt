package com.guarddog.jele

import android.opengl.Matrix

class Obstacle(x: Float, y: Float, z: Float) {
    var position = floatArrayOf(x, y, z)
    var isAlive = true
    private val body = Cube(floatArrayOf(0.6f, 0.2f, 0.2f, 1f))

    fun update(delta: Float) {
        // obstacles are static; we could animate them slightly
        body.setTransform(position, position[2] * 10f)
    }

    fun draw(vpMatrix: FloatArray) {
        if (!isAlive) return
        val mvp = FloatArray(16)
        Matrix.multiplyMM(mvp, 0, vpMatrix, 0, FloatArray(16).also { Matrix.setIdentityM(it, 0) }, 0)
        body.draw(vpMatrix)
    }
}
