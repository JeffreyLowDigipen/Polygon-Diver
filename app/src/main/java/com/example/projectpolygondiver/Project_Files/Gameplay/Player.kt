package com.example.projectpolygondiver.GameObjects

import org.joml.Vector3f
import com.example.projectpolygondiver.Managers.GameObjectManager
import android.util.Log

class Player : GameObject() {

    private val bulletCooldown = 0.3f // Time between shots
    private var timeSinceLastShot = 0f
    private var lastMoveDirection = Vector3f(0f, -1f, 0f) // Default shooting upwards

//    init {
//        //modelName = "plane"
//        position = Vector3f(0f, 0f, 0f) // Spawn player near the bottom
//        scale = Vector3f(0.5f, 0.5f, 0.5f)
//        color = Vector3f(0f, 0.5f, 1f) // Light blue color
//        movementSpeed=200f
//    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        timeSinceLastShot += deltaTime
       // Log.d ("Player","timeSinceLastShot: $timeSinceLastShot")
        // Continuously shoot in the last direction after the cooldown
        if (timeSinceLastShot >= bulletCooldown || timeSinceLastShot <-1) {
            shoot()
            //Log.d("Player" ,"Last moved Direction: ${lastMoveDirection.x},${lastMoveDirection.y}")
            timeSinceLastShot = 0f // Reset cooldown
        }
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
        Log.d("Player", "Shot fired from position: ${position.x}, ${position.y}, ${position.z} towards direction: $lastMoveDirection")
    }

}
