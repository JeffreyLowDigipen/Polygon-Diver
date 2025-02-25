package com.example.projectpolygondiver.GameObjects

import android.util.Log
import org.joml.Vector3f
import com.example.projectpolygondiver.Managers.GameObjectManager
import com.example.projectpolygondiver.PlayerScoreViewModel

class Bullet(
    startPosition: Vector3f,
    private val direction: Vector3f,
    private val speed: Float =10f,
    changeTo3D : Boolean =false
) : GameObject() {

    private val lifespan = 2f // Bullet lasts for 5 seconds
    private var timeAlive = 0f
    private var piercingValue=0

    init {
        modelName = "plane"
        position = Vector3f(startPosition)

        if(changeTo3D) {
            scale = Vector3f(0.4f, 0.4f, 0.4f) // Small bullet
            color = Vector3f(1f, 1f, 0f)
            piercingValue=1
        }
        else
        {
            scale = Vector3f(0.3f, 0.3f, 0.3f) // Small bullet
            color = Vector3f(1f, 0f, 0f) // Red bullet
        }
        //textureName = "Chicken_Tx"
        direction.normalize()

    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        // Move bullet forward based on its own direction and speed
        val movement = Vector3f(direction).mul(speed * deltaTime)
       // movement.y*=-1f;
        position.add(movement)
        //Log.d("Bullet", "Direction(Speed: $speed ): ${movement.x},${movement.y} ")
        //Log.d("Bullet", "Position(Speed: $speed ): ${position.x},${position.y} ")
        // Destroy bullet after its lifespan ends
        timeAlive += deltaTime
        if (timeAlive > lifespan) {
            GameObjectManager.removeGameObject(this)
        }
    }

    override fun onCollision(other: GameObject) {
        if (other !is Bullet) { // Ignore bullet-to-bullet collision

            if(other.type==GOType.ENEMY)
            {
                if(piercingValue<=0) {
                    GameObjectManager.removeGameObject(this)
                    --piercingValue
                }

                val player = GameObjectManager.Player as? Player
                if(player !=null)
                {
                    player.score++;
                    if(player.score>= player.targetScoreTo3D)
                    {
                        player.ChangePlayerTo3D()
                    }
                }
            }

        }
    }


}
