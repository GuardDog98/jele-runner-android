package com.guarddog.jele

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GameGLSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {
    val renderer: GameRenderer?

    init {
        setEGLContextClientVersion(2)
        renderer = GameRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
