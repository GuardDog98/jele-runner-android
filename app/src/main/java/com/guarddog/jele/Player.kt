package com.guarddog.jele

import android.opengl.GLES20
import android.opengl.Matrix
import kotlin.math.max

class Player {
    // position x,y,z
    val position = floatArrayOf(0f, 0.9f, 0f)
    var velocityY = 0f
    var rotationY = 0f

    private val laneWidth = 3.2f
    private val moveSpeed = 7f
    private val jumpForce = 8.5f
    private val gravity = 20f

    private var targetX = 0f

    private val cube = Cube()

    fun update(delta: Float) {
        // forward
        position[2] += moveSpeed * delta

        // horizontal lerp towards targetX
        position[0] = lerp(position[0], targetX, 12f * delta)

        // gravity / jump
        if (position[1] > 0.9f || velocityY > 0f) {
            velocityY -= gravity * delta
            position[1] += velocityY * delta
            if (position[1] <= 0.9f) {
                position[1] = 0.9f
                velocityY = 0f
            }
        }

        cube.setTransform(position, rotationY)
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
        cube.draw(vpMatrix)
    }

    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * max(0f, t)
    }
}
