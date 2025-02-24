package com.example.projectpolygondiver.GameObjects

import com.example.projectpolygondiver.Managers.GameObjectManager
import android.util.Log
import org.joml.Vector3f

class Skills {

    private var shrinkActive = false
    private var shrinkDuration = 5f
    private var shrinkTimer = 0f
    private var shrinkSpeed = 100f
    private var shrinkPercentage = 0.5f // Default percentage (50% of the original scale)

    // Store original scales for each enemy
    private val originalScales = mutableMapOf<GameObject, Vector3f>()

    // ✅ Direction tracking map
    private val directionMap = mutableMapOf(
        "left" to 0,
        "right" to 0,
        "up" to 0,
        "down" to 0
    )

    public var directionCheckTimer = 0f // Timer for checking directions
    private val directionCheckInterval = 5f // Interval to check every 5 seconds

    // Function to activate shrinking for all enemies with a percentage
    fun activateShrink(duration: Float, percentage: Float, shrinkSpeed: Float) {
        if (!shrinkActive) {
            this.shrinkActive = true
            this.shrinkDuration = duration
            this.shrinkTimer = 0f
            this.shrinkSpeed = shrinkSpeed
            this.shrinkPercentage = percentage

            // Save original scales and set target scales based on percentage
            val enemies = GameObjectManager.getAllGameObjects().filter {
                it.type == GameObject.GOType.ENEMY
            }

            originalScales.clear()
            for (enemy in enemies) {
                originalScales[enemy] = Vector3f(enemy.scale)
            }

            Log.d("Skills", "Enemy shrinking activated for $duration seconds at $percentage% scale.")
        }
    }

    // Function to be called from the main update loop
    fun update(deltaTime: Float) {


        if (directionCheckTimer >= directionCheckInterval) {
            checkDirections()
            directionCheckTimer = 0f // Reset timer after checking
        }

        // Disable shrinking after duration ends
        if (shrinkTimer >= shrinkDuration) {
            shrinkActive = false
            originalScales.clear()
            Log.d("Skills", "Enemy shrinking deactivated after $shrinkDuration seconds.")
        }

        if (!shrinkActive) return

        shrinkTimer += deltaTime
        directionCheckTimer += deltaTime

        // Shrink all enemies
        for ((enemy, originalScale) in originalScales) {
            val targetScale = Vector3f(
                originalScale.x * shrinkPercentage,
                originalScale.y * shrinkPercentage,
                originalScale.z * shrinkPercentage
            )
            shrinkEnemy(enemy, targetScale, shrinkSpeed * deltaTime)
        }


    }


    private fun checkDirections() : Boolean {
        val allTriggered = directionMap.values.all { it == 1 }
        if (allTriggered) {
            Log.d("DirectionCheck", "All directions triggered within 5 seconds!")
            return true
        } else {

            Log.d("DirectionCheck", "Not all directions were triggered. Resetting...")
            return false
        }

        // Reset all directions after check
        directionMap.keys.forEach { directionMap[it] = 0 }
    }

    // ✅ Function to mark a direction as triggered (can be called from tilt detection)
    fun markDirection(direction: String) {
        if (directionMap.containsKey(direction)) {
            directionMap[direction] = 1
            Log.d("DirectionTriggered", "$direction marked as triggered.")
        }
    }

    // Smoothly shrink an enemy toward the target percentage of its original scale
    private fun shrinkEnemy(enemy: GameObject, targetScale: Vector3f, shrinkAmount: Float) {
        val currentScale = enemy.scale

        // Interpolating each axis toward the target scale
        val newX = approach(currentScale.x, targetScale.x, shrinkAmount)
        val newY = approach(currentScale.y, targetScale.y, shrinkAmount)
        val newZ = approach(currentScale.z, targetScale.z, shrinkAmount)

        enemy.scale = Vector3f(newX, newY, newZ)

        // Log shrinking for debugging
        Log.d(
            "ShrinkEffect",
            "Shrinking ${enemy.modelName} -> Scale: (${newX}, ${newY}, ${newZ})"
        )
    }

    // Utility function to move a value toward the target smoothly
    private fun approach(current: Float, target: Float, delta: Float): Float {
        return when {
            current < target -> minOf(current + delta, target)
            current > target -> maxOf(current - delta, target)
            else -> current
        }
    }
}
