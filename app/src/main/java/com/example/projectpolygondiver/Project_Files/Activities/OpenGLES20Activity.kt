package com.example.projectpolygondiver.OpenGLActivity

import android.content.Context
import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.TextView
import android.widget.FrameLayout
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpolygondiver.GameObjects.GameObject
import com.example.projectpolygondiver.Graphics.MyGLRenderer
import com.example.projectpolygondiver.Managers.GameObjectManager
import com.example.projectpolygondiver.Managers.InputManager
import com.example.projectpolygondiver.R


class OpenGLES20Activity : AppCompatActivity() {

    private lateinit var gLView: MyGLSurfaceView
    private lateinit var fpsTextView: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the newly created XML layout
        setContentView(R.layout.activity_opengl)

        // Set up OpenGL surface view
        gLView = findViewById(R.id.glSurfaceView)

        // Initialize the FPS TextView
        fpsTextView = findViewById(R.id.fpsCounter)

        // Update the FPS display every second
        updateFPSTitle()
    }

    private fun updateFPSTitle() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val fps = GameObjectManager.fps
                fpsTextView.text = "FPS: $fps"
                handler.postDelayed(this, 1000) // Refresh every second
            }
        }, 1000)
    }
}
class MyGLSurfaceView : GLSurfaceView {
    private val renderer: MyGLRenderer

    // Constructor for XML inflation
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setEGLContextClientVersion(3)
        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
        setOnTouchListener(InputManager)
        Log.d("MyGLSurfaceView", "Renderer initialized successfully (from XML)")
    }

    // Existing constructor
    constructor(context: Context) : super(context) {
        setEGLContextClientVersion(3)
        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
        setOnTouchListener(InputManager)
        Log.d("MyGLSurfaceView", "Renderer initialized successfully")
    }
}
