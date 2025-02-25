package com.example.projectpolygondiver.GameObjects

import com.example.projectpolygondiver.Managers.GameObjectManager
import android.util.Log
import org.joml.Vector3f

class Player : GameObject() {

    private val bulletCooldown = 0.3f
    private var timeSinceLastShot = 0f
    private var lastMoveDirection = Vector3f(0f, -1f, 0f)
    private var targetYaw: Float = 0f

    //  Health and Invulnerability Variables
    var health = 3 // Starting health
    private var isInvulnerable = false
    private var invulnerabilityTimer = 0f
    private val invulnerabilityDuration = 2f // 2 seconds of invulnerability
    private val speedBoostMultiplier = 1.5f // Increase speed during invulnerability


    public var score: Int = 0
    public var targetScoreTo3D = 5
    public var changeTo3D = false
    public val skill: Skills = Skills()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)


        if (isInvulnerable) {
            invulnerabilityTimer += deltaTime
            renderActive=!renderActive;
            if (invulnerabilityTimer >= invulnerabilityDuration) {
                isInvulnerable = false
                movementSpeed /= speedBoostMultiplier // Reset speed
                renderActive=true
                Log.d("Player", "Invulnerability ended. Speed reset.")
            }
        }

        if (changeTo3D) rotateTowardsTarget(deltaTime)

        timeSinceLastShot += deltaTime

        if (timeSinceLastShot >= bulletCooldown || timeSinceLastShot < -1) {
            shoot()
            timeSinceLastShot = 0f
        }

        skill.update(deltaTime)
    }


    fun takeDamage(amount: Int) {
        if (!isInvulnerable) {
            health -= amount
            Log.d("Player", "Took damage! Remaining health: $health")

            // Activate invulnerability
            isInvulnerable = true
            invulnerabilityTimer = 0f
            movementSpeed *= speedBoostMultiplier // Boost movement speed during invulnerability
            Log.d("Player", "Invulnerability activated! Speed boosted.")
            scale.mul(0.7f)
            // Check if health is zero or below
            if (health <= 0) {
                onDeath()
            }
        } else {
            Log.d("Player", "Damage ignored due to invulnerability.")
        }
    }


    private fun onDeath() {
        Log.d("Player", "Player has died. Resetting game or triggering game over logic.")
        // Add game-over logic here if needed
    }


    fun onMove(direction: Vector3f) {
        if (direction.length() > 0f) {
            position.add(direction)
            lastMoveDirection.set(direction.normalize())
        }
    }


    private fun shoot() {
        val bullet = Bullet(Vector3f(position), Vector3f(lastMoveDirection), 10f, changeTo3D)
        GameObjectManager.addGameObject(bullet)
    }


    fun setTargetRotation(yaw: Float) {
        targetYaw = yaw
    }

    fun rotateTowardsTarget(deltaTime: Float) {
        val rotationSpeed = 5f
        val yawDifference = ((targetYaw - rotation.y + 540f) % 360f) - 180f
        rotation.y += yawDifference * rotationSpeed * deltaTime

        if (rotation.y >= 360f) rotation.y -= 360f
        if (rotation.y < 0f) rotation.y += 360f
    }


    fun ChangePlayerTo3D() {
        textureName = "robot2"
        modelName = "robot2"
        scale = Vector3f(0.07f, 0.07f, 0.07f)
        GameObjectManager.enemySpawner.changeTo3D = true
        changeTo3D = true
        rotation = Vector3f(90f, 0f, 0f)

    }
}
