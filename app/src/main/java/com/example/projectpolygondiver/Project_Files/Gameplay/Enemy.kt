package com.example.projectpolygondiver.GameObjects

import com.example.projectpolygondiver.Managers.GameObjectManager
import com.example.projectpolygondiver.Managers.CameraManager
import org.joml.Vector3f

class Enemy(startPosition: Vector3f) : GameObject() {
    private val speed = 2f

    init {
        position = startPosition
        scale = Vector3f(1f, 1f, 1f)
        color = Vector3f(1f, 0f, 0f) // Red color
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        // Move towards the camera center
        val playerPos = CameraManager.cameraPosition
        val direction = Vector3f(playerPos).sub(position).normalize()
        position.add(direction.mul(speed * deltaTime))
    }

    override fun onCollision(other: GameObject) {
        if (other is Bullet) {
            //GameObjectManager.removeGameObject(this) // Destroy enemy on bullet hit
        }
    }
}
