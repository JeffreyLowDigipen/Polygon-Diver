package com.example.projectpolygondiver.GameObjects

import android.app.Activity
import com.example.projectpolygondiver.Managers.GameObjectManager
import android.util.Log
import org.joml.Vector3f

class Skills {

    private var shrinkActive = false
    private var shrinkDuration = 1f
    private var shrinkTimer = 0f
    private var shrinkSpeed = 100f
    private var shrinkPercentage = 0.5f // Default percentage (50% of the original scale)

    // Store original scales for each enemy
    private val originalScales = mutableMapOf<GameObject, Vector3f>()

    // ✅ Direction tracking map
    /*private val directionMap = mutableMapOf(
        "left" to 0,
        "right" to 1,
        "up" to 2,
        "down" to 3
    )*/

    val arrowSequenceContainer = mutableListOf<String>() // Stores sequence of directions
    private var currentInputIndex = 0 // Tracks player's progress through the sequence

    var onArrowHighlight: ((String) -> Unit)? = null // Callback for UI updates

    init {
        generateArrowSequence() // Generate initial sequence
    }

    // Generate a random sequence of directions
    private fun generateArrowSequence() {
        arrowSequenceContainer.clear()
        val directions = listOf("up", "down", "left", "right")
        repeat(4) { arrowSequenceContainer.add(directions.random()) } // Generate 4 random directions
        Log.d("ArrowSequence", "Generated sequence: $arrowSequenceContainer")
    }

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
        // Disable shrinking after duration ends
        if (shrinkTimer >= shrinkDuration) {
            shrinkActive = false
            originalScales.clear()
            Log.d("Skills", "Enemy shrinking deactivated after $shrinkDuration seconds.")
        }

        if (!shrinkActive) return

        shrinkTimer += deltaTime

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

    // Validate player input against the sequence
    fun inputDirection(direction: String) {
        if (direction == arrowSequenceContainer.getOrNull(currentInputIndex)) {
            onArrowHighlight?.invoke(direction) // Highlight the corresponding arrow
            currentInputIndex++

            if (currentInputIndex >= arrowSequenceContainer.size) {
                Log.d("SequenceComplete", "Player completed the sequence!")
                resetSequence()
            }
        } else {
            Log.d("IncorrectTilt", "Wrong direction! Restarting sequence.")
            resetSequence()
        }
    }

    // Reset the sequence for a new round
    private fun resetSequence() {
        currentInputIndex = 0
        generateArrowSequence()
    }

    // Get the current sequence for UI rendering
    fun getArrowSequence(): List<String> {
        return arrowSequenceContainer
    }

    // Function to mark a direction as triggered (can be called from tilt detection)
    /*fun markDirection(direction: String) {
        if (directionMap.containsKey(direction)) {
            directionMap[direction] = 1
            Log.d("DirectionTriggered", "$direction marked as triggered.")
        }
    }

    fun resetDirections()
    {
        directionMap.keys.forEach { directionMap[it] = 0}
    }*/

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
