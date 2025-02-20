package com.example.projectpolygondiver

import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpolygondiver.Project_Files.Graphics.MyGLRenderer
import android.util.Log
// MainActivity launches the OpenGL Activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "Launch OPENGL")
        // Launch OpenGL Activity
        val intent = Intent(this, OpenGLES20Activity::class.java)
        startActivity(intent)

        // Finish this activity so the user doesn't come back to it when pressing back
        finish()
    }
}

// OpenGLES20Activity will use the custom MyGLSurfaceView
class OpenGLES20Activity : AppCompatActivity() {

    private lateinit var gLView: MyGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use custom MyGLSurfaceView
        gLView = MyGLSurfaceView(this)
        setContentView(gLView)
    }
}

// Custom GLSurfaceView for OpenGL Rendering
class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyGLRenderer

    init {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3)

        // Initialize the custom renderer
        renderer = MyGLRenderer(context)

        // Set the renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        // Optional: Set render mode to render only when data changes
        renderMode = RENDERMODE_CONTINUOUSLY

        Log.d("MyGLSurfaceView", "Renderer initialized successfully")
    }
}
