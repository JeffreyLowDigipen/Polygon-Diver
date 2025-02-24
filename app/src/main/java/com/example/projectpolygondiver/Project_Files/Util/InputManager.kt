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
import com.example.projectpolygondiver.Sensors.TiltSensorManager

//import java.lang.ref.WeakReference

object InputManager : View.OnTouchListener {

    private var isTouching = false
    private var screenCenter = Vector2f() // Center of the screen
    private var currentTouchPosition = Vector2f()
    public lateinit var tiltSensorManager : TiltSensorManager;
    //private var activity: WeakReference<Activity>? = null
    // Sensitivity and speed cap
    private val sensitivity = 0.01f
    private var currentVelocity = Vector3f()
    // ✅ New variables to control tilt detection
    var TriggerTiltReset = false
    var isDetectingTilt = false // Flag to track if tilt detection should be active
//    // Function to initialize the InputManager with the activity
//    fun setActivity(currentActivity: Activity) {
//        activity = WeakReference(currentActivity)
//    }
    // Set screen dimensions when initialized
    fun setScreenDimensions(screenWidth: Int, screenHeight: Int) {
        screenCenter.set(screenWidth / 2f, screenHeight / 2f)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                isTouching = true
                isDetectingTilt = true
                currentTouchPosition.set(event.x, event.y)

                processTouchInput()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouching = false
                isDetectingTilt = false
                currentVelocity.set(0f, 0f, 0f) // Stop movement when touch is released
                tiltSensorManager.resetRequested = false
            }
        }
        return true
    }

    private fun processTouchInput() {
        // Calculate movement direction based on the distance from screen center
        var deltaVector = Vector2f()

        deltaVector.x = (currentTouchPosition.x - screenCenter.x)
        deltaVector.y = (screenCenter.y - currentTouchPosition.y) // Inverted Y

        deltaVector.normalize()
        deltaVector.mul(GameObjectManager.deltaTime)

        currentVelocity.set(GameObjectManager.Player?.let { deltaVector.mul(it.movementSpeed) },0f)

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


    fun update(deltaTime: Float) {
        if (isTouching) {
            val movement = Vector3f(currentVelocity)

           if(!TriggerTiltReset)
           {
               TriggerTiltReset=true;
              var player= GameObjectManager.Player as Player
               player.skill.directionCheckTimer=0f;
               tiltSensorManager.ResetOrientation()
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
