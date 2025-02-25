package com.example.projectpolygondiver.OpenGLActivity

import com.example.projectpolygondiver.Sensors.ShakeSensorManager

import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.TextView
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.lifecycle.ViewModelProvider
import com.example.projectpolygondiver.AppNavHost
import com.example.projectpolygondiver.GameObjects.Player
import com.example.projectpolygondiver.GameObjects.PlayerDeathListener
import com.example.projectpolygondiver.GameObjects.Skills
import com.example.projectpolygondiver.Graphics.MyGLRenderer
import com.example.projectpolygondiver.Managers.CameraManager
import com.example.projectpolygondiver.Managers.GameObjectManager
import com.example.projectpolygondiver.Managers.InputManager
import com.example.projectpolygondiver.NavViewModel
import com.example.projectpolygondiver.PlayerScore
import com.example.projectpolygondiver.PlayerScoreDatabase
import com.example.projectpolygondiver.PlayerScoreRepository
import com.example.projectpolygondiver.PlayerScoreViewModel
import com.example.projectpolygondiver.PlayerScoreViewModelFactory
import com.example.projectpolygondiver.Project_Files.Gameplay.GameState
import com.example.projectpolygondiver.Project_Files.MainActivity
import com.example.projectpolygondiver.R
import org.joml.Vector3f


class OpenGLES20Activity : AppCompatActivity() {

  //  private lateinit var tiltDetector: SensorTiltActivity
    private lateinit var gLView: MyGLSurfaceView
    private val handler = Handler(Looper.getMainLooper())
  //  private lateinit var tiltSensorManager: TiltSensorManager
    private lateinit var skillButton: Button
    private var isbuttonpressed = false

    private lateinit var shakeSensorManager: ShakeSensorManager
    private val playerScoreViewModel: PlayerScoreViewModel by viewModels {
        val database = PlayerScoreDatabase.getDatabase(this)
        val repository = PlayerScoreRepository(database.playerScoreDao())
        PlayerScoreViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the newly created XML layout
        setContentView(R.layout.activity_opengl)
       // tiltDetector = SensorTiltActivity(this)
        // Set up OpenGL surface view
        gLView = findViewById(R.id.glSurfaceView)

        // Update the FPS display every second
        updateFPSTitle()
        // Initialize the tilt sensor manager
        shakeSensorManager = ShakeSensorManager(this)
        {
            handleShakeDetected()
        }
            //InputManager.tiltSensorManager = tiltSensorManager

        val stopButton = findViewById<View>(R.id.Stop)
        stopButton.setOnClickListener {
            showNameInputDialog(it)
        }

        val inputManager = InputManager

        val player = GameObjectManager.Player as? Player
        player?.setDeathListener(object : PlayerDeathListener {
            override fun onPlayerDeath() {
                runOnUiThread {
                    showNameInputDialog(gLView)
                }
            }
        })






        val (screenWidth, screenHeight) = CameraManager.getScreenDimensions(this@OpenGLES20Activity)
        //  val backgroundScale = CameraManager.getBackgroundScale(screenWidth, screenHeight)
        CameraManager.screenWidth= screenWidth.toFloat()
        CameraManager.screenHeight= screenHeight.toFloat()

        val skills = Skills()

        // Initialize custom arrow drawing view

        InputManager.initialize(skills)
    }
    private fun performClickAction() {
        // Custom click logic can be placed here if needed
        Log.d("SkillButton", "PerformClick called for accessibility")
    }
    fun showNameInputDialog(view: View)
    {


        GameObjectManager.pauseGame = true;

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null)
        val editText = dialogView.findViewById<EditText>(R.id.name_input)

        AlertDialog.Builder(this)
            .setTitle("Enter Your Name")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val playerName = editText.text.toString()
                if (playerName.isNotEmpty()) {
                    saveNameToDatabase(playerName)
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun saveNameToDatabase(name: String) {
        val player= GameObjectManager.Player as Player
        playerScoreViewModel.insertScore(name, player.score) // Save to database
        Toast.makeText(this, "Name saved!", Toast.LENGTH_SHORT).show()

        // Intent to navigate back to MainActivity and clear back stack
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("navigateToMainMenu", true) // Pass a signal to navigate to mainMenu
        }
        startActivity(intent)
        GameObjectManager.pauseGame = false;
        finish() // Finish the OpenGLES20Activity
    }

    private fun handleShakeDetected() {
         Log.d("ShakeDetection", "Shake detected! Performing action.")
         val player =GameObjectManager.Player as Player
         player.skill.activateShrink(0.5f,0.3f,5f)
    }

    override fun onPause() {
        super.onPause()
        shakeSensorManager.stopListening() // Stop shake detection when paused
    }

    override fun onResume() {
        super.onResume()
        shakeSensorManager.startListening()
        }



        private fun updateFPSTitle() {
        handler.postDelayed(object : Runnable {
            override fun run() {
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
