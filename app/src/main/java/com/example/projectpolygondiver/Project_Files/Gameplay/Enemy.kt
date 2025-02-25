package com.example.projectpolygondiver.GameObjects

import com.example.projectpolygondiver.Managers.GameObjectManager
import org.joml.Vector3f
import kotlin.math.atan2


class Enemy(startPosition: Vector3f, changeto3D : Boolean) : GameObject() {

    private var ChangeTo3D : Boolean = false;
    init {
        position = startPosition
        scale = Vector3f(1f, 1f, 1f)
        color = Vector3f(0f, 0f, 0f) // Red color
        if(!changeto3D) {
            modelName = "plane"
            textureName = "virus"
        }
        else
        {
            modelName = "robot"
            textureName = "robot"
            scale = Vector3f(0.6f, 0.6f, 0.6f)
            color = Vector3f(1f,1f,1f)
        }
        ChangeTo3D=changeto3D
        movementSpeed = 0.5f
        type = GOType.ENEMY
    }

    override fun update(deltaTime: Float) {


        // Move towards the player
        val playerPos = GameObjectManager.Player?.position
        if (playerPos != null) {
            val direction = Vector3f(playerPos).sub(position).normalize()
            position.add(direction.mul(movementSpeed * deltaTime))

            //  Rotate toward the player
          if(ChangeTo3D)
                rotateTowardsPlayer(playerPos)
        }
        super.update(deltaTime)
    }


    private fun rotateTowardsPlayer(playerPosition: Vector3f) {
        // Calculate the direction vector (ignoring Z-axis since it doesn't matter)
        val direction = Vector3f(playerPosition.x - position.x, playerPosition.y - position.y, 0f)

        // Calculate the angle in radians based on X and Y positions
        val angleInRadians = atan2(direction.y, direction.x) // Use X and Y for top-down rotation
        var targetAngle = Math.toDegrees(angleInRadians.toDouble()).toFloat()

        // Normalize the angle to stay between 0° and 360°
        if (targetAngle < 0) {
            targetAngle += 360f
        }

        //  Apply rotation: Rotate toward the player on the Y-axis and offset X by 90°
        rotation.y = smoothRotate(rotation.y, targetAngle, 5f) // Smooth Y-axis rotation
        rotation.x = 90f // Lock X-axis rotation for proper top-down alignment
    }

    //  Helper function for smooth rotation using linear interpolation (lerp)
    private fun smoothRotate(current: Float, target: Float, speed: Float): Float {
        val delta = ((target - current + 540f) % 360f) - 180f // Shortest path rotation
        return current + delta * 0.1f * speed // Smooth interpolation towards the target
    }







    override fun onCollision(other: GameObject) {
        if (other is Bullet) {
            GameObjectManager.removeGameObject(this) // Destroy enemy on bullet hit
        }
        if (other is Player)
        {
            val player = GameObjectManager.Player as Player
            player.takeDamage(1);
        }
    }
}
