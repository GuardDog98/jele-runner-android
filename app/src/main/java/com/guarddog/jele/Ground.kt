package com.guarddog.jele

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Ground {
    private val vertices = floatArrayOf(
        -20f, 0f,  0f,
         20f, 0f,  0f,
        -20f, 0f, 200f,
         20f, 0f, 200f
    )

    private val indices = shortArrayOf(0,1,2,1,3,2)
    private val color = floatArrayOf(0.11f, 0.45f, 0.18f, 1f)

    private val vertexBuffer: FloatBuffer
    private val indexBuffer = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder()).asShortBuffer()

    private val vertexStride = 3 * 4
    private var program = 0
    private val modelMatrix = FloatArray(16)

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.put(vertices).position(0)

        indexBuffer.put(indices).position(0)

        val vShaderCode = """
            uniform mat4 uMVPMatrix;
            attribute vec4 vPosition;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
            }
        """.trimIndent()

        val fShaderCode = """
            precision mediump float;
            uniform vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """.trimIndent()

        val vShader = loadShader(GLES20.GL_VERTEX_SHADER, vShaderCode)
        val fShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vShader)
            GLES20.glAttachShader(it, fShader)
            GLES20.glLinkProgram(it)
        }

        Matrix.setIdentityM(modelMatrix, 0)
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

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(posHandle)
    }

    private fun loadShader(type: Int, code: String): Int {
        return GLES20.glCreateShader(type).also {
            GLES20.glShaderSource(it, code)
            GLES20.glCompileShader(it)
        }
    }
}
