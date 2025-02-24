package com.example.projectpolygondiver.Managers

import android.util.Log
import com.example.projectpolygondiver.GameObjects.Enemy
import org.joml.Vector3f
import kotlin.random.Random

object EnemySpawner {
    private var spawnCooldown = 3f // Time interval between spawns (in seconds)
    private var timeSinceLastSpawn = 0f
    private val enemiesPerSpawn = 3 // Number of enemies to spawn at once
    private val spawnDistanceMultiplier = 0.5f // Distance factor outside of the screen

    fun update(deltaTime: Float) {
        timeSinceLastSpawn += deltaTime

        if (timeSinceLastSpawn >= spawnCooldown ) {
            spawnEnemies(enemiesPerSpawn)
            timeSinceLastSpawn = 0f // Reset the timer
        }
    }

    private fun spawnEnemies(count: Int) {
        Log.d("Enemy","Enemy Spawned")
        for (i in 1..count) {
            val spawnPosition = generateSpawnPositionOutsideCamera()
            val enemy = Enemy(spawnPosition)
            GameObjectManager.addGameObject(enemy)
            Log.d("Enemy","Enemy Spawned")
        }
    }

    private fun generateSpawnPositionOutsideCamera(): Vector3f {
        val cameraPos = CameraManager.cameraPosition
        val cameraScale = CameraManager.getBackgroundScale(1080, 2400) // Assuming screen resolution
        val offsetX = cameraScale.x * spawnDistanceMultiplier
        val offsetY = cameraScale.y * spawnDistanceMultiplier

        // Randomly decide which side of the screen to spawn the enemy
        return when (Random.nextInt(4)) {
            0 -> Vector3f(cameraPos.x - offsetX, Random.nextFloat() * offsetY * 2 - offsetY, 0f) // Left
            1 -> Vector3f(cameraPos.x + offsetX, Random.nextFloat() * offsetY * 2 - offsetY, 0f) // Right
            2 -> Vector3f(Random.nextFloat() * offsetX * 2 - offsetX, cameraPos.y + offsetY, 0f) // Top
            else -> Vector3f(Random.nextFloat() * offsetX * 2 - offsetX, cameraPos.y - offsetY, 0f) // Bottom
        }
    }
}