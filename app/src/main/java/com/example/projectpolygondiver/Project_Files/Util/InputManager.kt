package com.example.projectpolygondiver.Managers

import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.projectpolygondiver.GameObjects.Player
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.sqrt

object InputManager : View.OnTouchListener {

    private var isTouching = false
    private var screenCenter = Vector2f() // Center of the screen
    private var currentTouchPosition = Vector2f()

    // Sensitivity and speed cap
    private val sensitivity = 0.01f
    private var currentVelocity = Vector3f()

    // Set screen dimensions when initialized
    fun setScreenDimensions(screenWidth: Int, screenHeight: Int) {
        screenCenter.set(screenWidth / 2f, screenHeight / 2f)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                isTouching = true
                currentTouchPosition.set(event.x, event.y)
                processTouchInput()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouching = false
                currentVelocity.set(0f, 0f, 0f) // Stop movement when touch is released
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


    }


    fun update(deltaTime: Float) {
        if (isTouching) {
            val movement = Vector3f(currentVelocity)

            // Move camera
            CameraManager.move(movement)

            // Move player
            GameObjectManager.Player?.let { player ->
                (player as? Player)?.onMove(movement)
            }
        }
    }
}
