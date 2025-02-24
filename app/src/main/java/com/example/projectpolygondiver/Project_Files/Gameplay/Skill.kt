package com.example.projectpolygondiver.GameObjects

import com.example.projectpolygondiver.Managers.GameObjectManager
import android.util.Log
import org.joml.Vector3f

class Skills {

    // Boolean to trigger shrinking
    private var shrinkActive = false
    private var shrinkDuration = 5f // Total duration for shrinking
    private var shrinkTimer = 0f // Timer to track shrinking time
    private var shrinkSpeed = 100f // Speed at which enemies shrink
    private var targetScale = Vector3f(0.1f, 0.1f, 0.1f) // Default target scale

    // Function to activate shrinking for all enemies
    fun activateShrink(duration: Float, targetScale: Vector3f, shrinkSpeed: Float) {
        if (!shrinkActive) {
            this.shrinkActive = true
            this.shrinkDuration = duration
            this.shrinkTimer = 0f
            this.targetScale = targetScale
            this.shrinkSpeed = shrinkSpeed
            Log.d("Skills", "Enemy shrinking activated for $duration seconds.")
        }
    }

    // Function to be called from the main update loop
    fun update(deltaTime: Float) {
        if (!shrinkActive) return

        shrinkTimer += deltaTime

        // Shrink all enemies
        val enemies = GameObjectManager.getAllGameObjects().filter {
            it.type == GameObject.GOType.ENEMY
        }

        for (enemy in enemies) {
            shrinkEnemy(enemy, targetScale, shrinkSpeed * deltaTime)
        }

        // Disable shrinking after duration ends
        if (shrinkTimer >= shrinkDuration) {
            shrinkActive = false
            Log.d("Skills", "Enemy shrinking deactivated after $shrinkDuration seconds.")
        }
    }

    // Smoothly shrink an enemy towards the target scale
    private fun shrinkEnemy(enemy: GameObject, targetScale: Vector3f, shrinkAmount: Float) {
        val currentScale = enemy.scale

        // Interpolating each axis towards the target scale
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
