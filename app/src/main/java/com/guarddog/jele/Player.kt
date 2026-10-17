package com.guarddog.jele

import android.opengl.Matrix
import kotlin.math.max

class Player {
    val position = floatArrayOf(0f, 0.9f, 0f)
    var velocityY = 0f
    var rotationY = 0f
    var color = floatArrayOf(0.22f, 0.70f, 0.95f, 1f)

    private val laneWidth = 3.2f
    private val moveSpeed = 7.3f
    private val jumpForce = 8.8f
    private val gravity = 22f
    private var targetX = 0f

    private val body = Cube(color)
    @JvmName("applyColor")
    fun setColor(newColor: FloatArray) {
        color = newColor
        body.setColor(newColor)
    }

    fun update(delta: Float) {
        position[2] += moveSpeed * delta
        position[0] = lerp(position[0], targetX, 12f * delta)

        if (position[1] > 0.9f || velocityY > 0f) {
            velocityY -= gravity * delta
            position[1] += velocityY * delta
            if (position[1] <= 0.9f) {
                position[1] = 0.9f
                velocityY = 0f
            }
        }

        body.setTransform(position, rotationY)
    }

    fun moveLeft() {
        targetX -= laneWidth
        if (targetX < -laneWidth * 2f) targetX = -laneWidth * 2f
    }

    fun moveRight() {
        targetX += laneWidth
        if (targetX > laneWidth * 2f) targetX = laneWidth * 2f
    }

    fun jump() {
        if (position[1] <= 0.91f) {
            velocityY = jumpForce
        }
    }

    fun draw(vpMatrix: FloatArray) {
        body.draw(vpMatrix)
    }

    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * max(0f, t)
    }
}
