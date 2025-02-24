package com.example.projectpolygondiver.GameObjects

import com.example.projectpolygondiver.Managers.GameObjectManager
import com.example.projectpolygondiver.Managers.CameraManager
import org.joml.Vector3f

class Enemy(startPosition: Vector3f) : GameObject() {


    init {
        position = startPosition
        scale = Vector3f(1f, 1f, 1f)
        color = Vector3f(1f, 0f, 0f) // Red color
        modelName = "cube"
        textureName = "Chicken_Tx"
        movementSpeed = 1f
        type=GOType.ENEMY
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        // Move towards the camera center
        val playerPos = GameObjectManager.Player?.position
        val direction = Vector3f(playerPos).sub(position).normalize()
        position.add(direction.mul(movementSpeed * deltaTime))
    }

    override fun onCollision(other: GameObject) {
        if (other is Bullet) {
            GameObjectManager.removeGameObject(this) // Destroy enemy on bullet hit
        }
    }
}