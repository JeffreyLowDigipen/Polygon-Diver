package com.example.projectpolygondiver.Managers

import android.util.Log
import com.example.projectpolygondiver.GameObjects.GameObject
import com.example.projectpolygondiver.GameObjects.Player
import com.example.projectpolygondiver.Graphics.MyGLRenderer
import com.example.projectpolygondiver.Managers.GameObjectManager.addObjects
import org.joml.Vector3f

object GameObjectManager {
    // List of all active game objects
    private val gameObjects: MutableList<GameObject> = mutableListOf()
    private val deleteObjects: MutableList<GameObject> = mutableListOf()
    private val addObjects: MutableList<GameObject> = mutableListOf()

    public var enemySpawner : EnemySpawner = EnemySpawner;

    public var frameCount = 0
    public var lastTime = System.currentTimeMillis()
    public var pauseGame =  false
    var Player: GameObject? = null
    val camera = CameraManager

    var deltaTime: Float = 0f;



    private var onPlayerInitialized: (() -> Unit)? = null

    fun initializePlayer() {

        onPlayerInitialized?.invoke()
    }

    fun setOnPlayerInitializedListener(listener: () -> Unit) {
        onPlayerInitialized = listener
    }

    // Add a new game object to the manager
    fun addGameObject(gameObject: GameObject): GameObject {
        gameObject.active = true;
        gameObject.renderActive=true;
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
    fun ReloadGame()
    {
        clearAllGameObjects()
        //val (screenWidth, screenHeight) = CameraManager.getScreenDimensions()
        //val backgroundScale = CameraManager.getBackgroundScale(screenWidth, screenHeight)


            val player = Player().apply {
                modelName = "plane"
                position = Vector3f(0f, 0f, 0f)
                rotation = Vector3f(0f, 0f, 0f)
                scale = Vector3f(1f, 1f, 1f)
                color = Vector3f(0f, 0f, 0f)
                textureName = "microwave"
                type = GameObject.GOType.PLAYER
                movementSpeed = 3f
            }

            Player=player;


        val background = GameObject().apply {
            modelName = "plane"
            position = Vector3f(0f, 0f, -1f)
            val tempscale = CameraManager.CheckBackgroundScale()
            scale = tempscale.mul(3f)
            Log.d("Camera","Scale: ${scale.x}, ${scale.y},${scale.z}")
            color = Vector3f(0.01f, 0.01f, 0.01f)
            textureName = "background"
            type = GameObject.GOType.DEFAULT
        }

        addGameObject(background)
        addGameObject(player)
        //this.Player?.let { addGameObject(it) }
      //  // Call this when the game or player is fully initialized
        GameObjectManager.initializePlayer()

        CameraManager.backgroundGO = background

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



    }
    // Update all game objects (called every frame)
    public fun update(deltaTime: Float) {
        enemySpawner.update(deltaTime)
    }

}
