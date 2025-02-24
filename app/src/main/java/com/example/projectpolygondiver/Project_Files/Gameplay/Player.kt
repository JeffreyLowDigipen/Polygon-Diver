package com.example.projectpolygondiver.GameObjects

import org.joml.Vector3f
import com.example.projectpolygondiver.Managers.GameObjectManager
import android.util.Log
import java.lang.Math.toDegrees
import kotlin.math.asin
import kotlin.math.atan2

class Player : GameObject() {

    private val bulletCooldown = 0.3f // Time between shots
    private var timeSinceLastShot = 0f
    private var lastMoveDirection = Vector3f(0f, -1f, 0f) // Default shooting upwards
    private var targetYaw: Float = 0f
    public val skill:Skills =Skills()
//    init {
//        //modelName = "plane"
//        position = Vector3f(0f, 0f, 0f) // Spawn player near the bottom
//        scale = Vector3f(0.5f, 0.5f, 0.5f)
//        color = Vector3f(0f, 0.5f, 1f) // Light blue color
//        movementSpeed=200f
//    }

    override fun update(deltaTime: Float) {


//        // Make the model face the direction you're moving/looking
//        if (lastMoveDirection.length() > 0) {
//            if (lastMoveDirection.length() > 0.01f) {
//                // Rotate smoothly on Y-axis based on the direction
//                val targetYaw = computeTopDownYaw(lastMoveDirection)
//                val yawDifference = ((targetYaw - rotation.y + 540f) % 360f) - 180f
//                rotation.y += yawDifference * (5f * deltaTime) // Smooth rotation with speed factor
//            }
//        }

        super.update(deltaTime)
        rotateTowardsTarget(deltaTime)


        timeSinceLastShot += deltaTime
       // Log.d ("Player","timeSinceLastShot: $timeSinceLastShot")
        // Continuously shoot in the last direction after the cooldown
        if (timeSinceLastShot >= bulletCooldown || timeSinceLastShot <-1) {
            shoot()
            //Log.d("Player" ,"Last moved Direction: ${lastMoveDirection.x},${lastMoveDirection.y}")
            timeSinceLastShot = 0f // Reset cooldown
        }
        skill.update(deltaTime)
    }
    // Function to set the target rotation based on touch input
    fun setTargetRotation(yaw: Float) {
        targetYaw = yaw
    }

    // Smoothly rotate toward the target yaw
    fun rotateTowardsTarget(deltaTime: Float) {
        val rotationSpeed = 5f // Rotation speed factor

        // Calculate shortest rotation distance
        val yawDifference = ((targetYaw - rotation.y + 540f) % 360f) - 180f
       // Log.d("Player" , "$yawDifference")
        // Smoothly interpolate the rotation
        rotation.y += yawDifference * rotationSpeed * deltaTime

        // Ensure rotation remains within 0-360 degrees
        if (rotation.y >= 360f) rotation.y -= 360f
        if (rotation.y < 0f) rotation.y += 360f
    }

    // Call this function externally when the player moves
    fun onMove(direction: Vector3f) {
        if (direction.length() > 0f) { // Check if the movement vector is valid
            position.add(direction)
            lastMoveDirection.set(direction.normalize()) // Update last movement direction

        }


    }

    private fun shoot() {
        val bullet = Bullet(Vector3f(position), Vector3f(lastMoveDirection)) // Shoot in the last movement direction
        GameObjectManager.addGameObject(bullet)
       // Log.d("Player", "Shot fired from position: ${position.x}, ${position.y}, ${position.z} towards direction: $lastMoveDirection")
    }
    // Function to rotate the model towards a direction
    private fun faceDirection(direction: Vector3f) {
        // Normalize the direction vector
        val normalizedDirection = direction.normalize()

        // Calculate yaw (rotation around Y-axis)
        val yaw = Math.toDegrees(
            atan2(
                normalizedDirection.x.toDouble(),
                normalizedDirection.z.toDouble()
            )
        ).toFloat()

        // Calculate pitch (rotation around X-axis)
        val pitch = Math.toDegrees(asin(-normalizedDirection.y.toDouble())).toFloat()
    }
}
