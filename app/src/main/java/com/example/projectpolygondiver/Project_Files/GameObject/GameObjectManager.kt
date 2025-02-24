package com.example.projectpolygondiver.Managers

import com.example.projectpolygondiver.GameObjects.GameObject
import com.example.projectpolygondiver.Graphics.MyGLRenderer

object GameObjectManager {
    // List of all active game objects
    private val gameObjects: MutableList<GameObject> = mutableListOf()
    private val deleteObjects: MutableList<GameObject> = mutableListOf()
    private val addObjects: MutableList<GameObject> = mutableListOf()


    public var frameCount = 0
    public var fps = 0
    public var lastTime = System.currentTimeMillis()

    var Player: GameObject? = null
    val camera = CameraManager

    var deltaTime: Float = 0f;

    // Add a new game object to the manager
    fun addGameObject(gameObject: GameObject): GameObject {
        gameObject.active = true;
        addObjects.add(gameObject)
        return gameObject
    }

    // Remove a game object from the manager
    fun removeGameObject(gameObject: GameObject) {
        deleteObjects.add(gameObject);
    }

    fun PreUpdateAddGO() {
        for (gameObject in addObjects.toList()) {
            gameObjects.add(gameObject)
        }
        addObjects.clear();
    }

    fun removeGameObjectOnPostUpdate() {
        for (gameObject in deleteObjects.toList()) {
            gameObjects.remove(gameObject)
        }
        deleteObjects.clear() // Clear the entire list after removal
    }

    fun getAllGameObjects(): MutableList<GameObject> {
        return gameObjects;
    }

    // Clear all game objects from the manager
    fun clearAllGameObjects() {
        gameObjects.clear()
        addObjects.clear()
        deleteObjects.clear()
    }

    public fun checkCollisions() {
        for (i in gameObjects.indices) {
            for (j in i + 1 until gameObjects.size) {
                val objA = gameObjects[i]
                val objB = gameObjects[j]
                if (objA.isCollidingWith(objB)) {
                    objA.onCollision(objB)
                    objB.onCollision(objA)
                }
            }
        }
        // Update all game objects (called every frame)
        fun update(deltaTime: Float) {
            // for (gameObject in gameObjects) {
            //    gameObject.update(deltaTime)
            // }

            // removeGameObjectOnPostUpdate();
        }


    }
}
