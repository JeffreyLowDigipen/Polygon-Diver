package com.example.projectpolygondiver.Managers

import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.projectpolygondiver.GameObjects.Player
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.atan2
import kotlin.math.sqrt
import android.view.KeyEvent
import androidx.core.view.KeyEventDispatcher.dispatchKeyEvent
import android.app.Activity
import android.widget.Button
import androidx.compose.material3.Button
import com.example.projectpolygondiver.R
//import com.example.projectpolygondiver.Sensors.TiltSensorManager
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalContext
import com.example.projectpolygondiver.GameObjects.Skills
import com.example.projectpolygondiver.Project_Files.Gameplay.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

//import java.lang.ref.WeakReference

object InputManager : View.OnTouchListener {
    private var highlightCallback: ((String) -> Unit)? = null
    private var resetCallback: (() -> Unit)? = null
    private var isTouching = false
    private lateinit var skills: Skills
    private lateinit var arrowViews: Map<String, ImageView>
    private var screenCenter = Vector2f() // Center of the screen
    private var currentTouchPosition = Vector2f()
   // public lateinit var tiltSensorManager : TiltSensorManager;
    //private var activity: WeakReference<Activity>? = null
    // Sensitivity and speed cap
    private val sensitivity = 0.01f
    private var currentVelocity = Vector3f()
    // ✅ New variables to control tilt detection
    var TriggerTiltReset = false
    //var isDetectingTilt = false // Flag to track if tilt detection should be active
//    // Function to initialize the InputManager with the activity
//    fun setActivity(currentActivity: Activity) {
//        activity = WeakReference(currentActivity)
//    }

    // Initialize Skills and arrow references
    fun initialize(skills: Skills) {
        this.skills = skills
        this.highlightCallback = highlightCallback
        this.resetCallback = resetCallback
    }

    // Call this when detecting a tilt input
    fun inputDirection(direction: String) {
        skills.inputDirection(direction)
        highlightCallback?.invoke(direction)
    }

    // Set screen dimensions when initialized
    fun setScreenDimensions(screenWidth: Int, screenHeight: Int) {
        screenCenter.set(screenWidth / 2f, screenHeight / 2f)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                isTouching = true
                //isDetectingTilt = true
                currentTouchPosition.set(event.x, event.y)

                processTouchInput()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouching = false
               // isDetectingTilt = false
                currentVelocity.set(0f, 0f, 0f) // Stop movement when touch is released
             //   tiltSensorManager.resetRequested = false
                resetArrows()
            }
        }
        return true
    }

    // Display the arrow sequence on screen
    private fun showArrowSequence(sequence: List<String>) {
        sequence.forEach { direction ->
            arrowViews[direction]?.visibility = View.VISIBLE
        }
    }

    // Highlight arrow when the correct tilt is detected
    private fun highlightArrow(direction: String) {
        arrowViews[direction]?.setColorFilter(android.graphics.Color.GREEN)
    }

    // Hide all arrows when touch ends
    private fun hideArrows() {
        arrowViews.values.forEach { arrow ->
            arrow.visibility = View.GONE
            arrow.clearColorFilter() // Reset arrow color
        }

        resetCallback?.invoke()
    }

    fun resetArrows()
    {
        resetCallback?.invoke()
    }

    private fun processTouchInput() {
        // Calculate movement direction based on the distance from screen center
        var deltaVector = Vector2f(0f, 0f)

        deltaVector.x = (currentTouchPosition.x - screenCenter.x)
        deltaVector.y = (screenCenter.y - currentTouchPosition.y) // Inverted Y

        deltaVector.normalize()
        deltaVector.mul(GameObjectManager.deltaTime)

        val player = GameObjectManager.Player as Player
        currentVelocity= Vector3f(deltaVector,0f).mul(player.movementSpeed)

        // Calculate the angle (in radians) and convert to degrees

        val angleInRadians = atan2(-deltaVector.x, deltaVector.y) // atan2(y, x) gives angle from center
        val angleInDegrees = Math.toDegrees(angleInRadians.toDouble()).toFloat() +180f

        // Normalize the angle (0° - 360°)
        val normalizedAngle = if (angleInDegrees < 0) angleInDegrees + 360f else angleInDegrees

        // Update the target rotation angle
        GameObjectManager.Player?.let { player ->
            (player as? Player)?.setTargetRotation(normalizedAngle)
        }
      //  Log.d("Player", "NormalizedAngle: ${normalizedAngle}")
    }

    fun skillUp(view: View)
    {
        showArrowSequence(skills.getArrowSequence())
    }

    fun update(deltaTime: Float) {
        if (isTouching) {
            val movement = Vector3f(currentVelocity)

           if(!TriggerTiltReset)
           {
               TriggerTiltReset=true;
              var player= GameObjectManager.Player as Player
            //   tiltSensorManager.ResetOrientation()
           }


            // Move camera
            CameraManager.move(movement)

            // Move player
            GameObjectManager.Player?.let { player ->
                (player as? Player)?.onMove(movement)
                    //(player as? Player)?.rotateTowardsTouch(movement, deltaTime)
            }
        }
    }

}
