package com.example.projectpolygondiver.OpenGLActivity

import com.example.projectpolygondiver.Sensors.TiltSensorManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.TextView
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.projectpolygondiver.GameObjects.Player
import com.example.projectpolygondiver.Graphics.MyGLRenderer
import com.example.projectpolygondiver.Managers.GameObjectManager
import com.example.projectpolygondiver.Managers.InputManager
import com.example.projectpolygondiver.R
import org.joml.Vector3f


class OpenGLES20Activity : AppCompatActivity() {

  //  private lateinit var tiltDetector: SensorTiltActivity
    private lateinit var gLView: MyGLSurfaceView
    private lateinit var fpsTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tiltSensorManager: TiltSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the newly created XML layout
        setContentView(R.layout.activity_opengl)
       // tiltDetector = SensorTiltActivity(this)
        // Set up OpenGL surface view
        gLView = findViewById(R.id.glSurfaceView)

        // Initialize the FPS TextView
        fpsTextView = findViewById(R.id.fpsCounter)

        // Update the FPS display every second
        updateFPSTitle()
        // Initialize the tilt sensor manager
        tiltSensorManager = TiltSensorManager(this)
        InputManager.tiltSensorManager = tiltSensorManager
    }

    override fun onPause() {
        super.onPause()
        tiltSensorManager.stopListening() // Stop detecting tilt when activity is paused
    }

    override fun onResume() {
        super.onResume()
        tiltSensorManager = TiltSensorManager(this) // Restart tilt detection when resumed
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
    // Detect when a key is pressed down
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            Log.d("Input" ,"Space is pressed")
            val playerObject = GameObjectManager.Player as? Player
            playerObject?.skill?.activateShrink(5f, 0.4f,1f)

            return true // Consume the event
        }
        return super.onKeyDown(keyCode, event)
    }

    // Detect when a key is released
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            Log.d("Input" ,"Space is released")
            return true // Consume the event
        }
        return super.onKeyUp(keyCode, event)
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
        this.isFocusableInTouchMode=true
        this.requestFocus()
        setOnTouchListener(InputManager)


        Log.d("MyGLSurfaceView", "Renderer initialized successfully (from XML)")
    }

    // Existing constructor
    constructor(context: Context) : super(context) {
        setEGLContextClientVersion(3)
        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
        this.isFocusableInTouchMode=true
        this.requestFocus()
        setOnTouchListener(InputManager)
        Log.d("MyGLSurfaceView", "Renderer initialized successfully")
    }


}
