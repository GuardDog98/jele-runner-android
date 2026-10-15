package com.guarddog.jele

import android.opengl.GLES20
import android.opengl.Matrix

class Coin(x: Float, y: Float, z: Float) {
    var position = floatArrayOf(x, y, z)
    var isAlive = true
    private val coinBody = Cube(floatArrayOf(1f, 0.82f, 0.28f, 1f))

    fun update(delta: Float) {
        position[1] = 1.2f + (0.18f * kotlin.math.sin(position[2] * 0.5f + delta * 10f))
        coinBody.setTransform(position, position[2] * 30f)
    }

    fun draw(vpMatrix: FloatArray) {
        if (!isAlive) return
        val scale = 0.3f
        val model = FloatArray(16)
        Matrix.setIdentityM(model, 0)
        Matrix.translateM(model, 0, position[0], position[1], position[2])
        Matrix.scaleM(model, 0, scale, scale, scale)
        Matrix.rotateM(model, 0, position[2] * 25f, 0f, 1f, 0f)

        val mvp = FloatArray(16)
        Matrix.multiplyMM(mvp, 0, vpMatrix, 0, model, 0)

        // draw coin using same cube mesh but with a custom transform route
        coinBody.setTransform(position, position[2] * 30f)
        coinBody.draw(vpMatrix)
    }
}
