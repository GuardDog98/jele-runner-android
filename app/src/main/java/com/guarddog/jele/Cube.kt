package com.guarddog.jele

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube(initialColor: FloatArray = floatArrayOf(0.22f, 0.70f, 0.95f, 1f)) {
    private val vertexCoords = floatArrayOf(
        -0.5f,  1.0f,  0.5f,
         0.5f,  1.0f,  0.5f,
        -0.5f,  0.0f,  0.5f,
         0.5f,  0.0f,  0.5f,
        -0.5f,  1.0f, -0.5f,
         0.5f,  1.0f, -0.5f,
        -0.5f,  0.0f, -0.5f,
         0.5f,  0.0f, -0.5f
    )

    private val drawOrder = shortArrayOf(
        0, 1, 2, 1, 3, 2,
        4, 5, 6, 5, 7, 6,
        4, 0, 6, 0, 2, 6,
        1, 5, 3, 5, 7, 3,
        4, 5, 0, 5, 1, 0,
        2, 3, 6, 3, 7, 6
    )

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val vertexStride = 3 * 4
    private val modelMatrix = FloatArray(16)
    private var color = initialColor.copyOf()

    private var program = 0

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.put(vertexCoords).position(0)

        indexBuffer = ByteBuffer.allocateDirect(drawOrder.size * 2).order(ByteOrder.nativeOrder()).asShortBuffer()
        indexBuffer.put(drawOrder).position(0)

        val vertexShader = """
            uniform mat4 uMVPMatrix;
            attribute vec4 vPosition;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
            }
        """.trimIndent()

        val fragmentShader = """
            precision mediump float;
            uniform vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """.trimIndent()

        val vShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vShader)
            GLES20.glAttachShader(it, fShader)
            GLES20.glLinkProgram(it)
        }

        Matrix.setIdentityM(modelMatrix, 0)
    }

    fun setColor(newColor: FloatArray) {
        color = newColor.copyOf()
    }

    fun setTransform(position: FloatArray, rotationY: Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, position[0], position[1], position[2])
        Matrix.rotateM(modelMatrix, 0, rotationY, 0f, 1f, 0f)
    }

    fun draw(vpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val posHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        val mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer)

        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        val mvp = FloatArray(16)
        Matrix.multiplyMM(mvp, 0, vpMatrix, 0, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvp, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
        GLES20.glDisableVertexAttribArray(posHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}
