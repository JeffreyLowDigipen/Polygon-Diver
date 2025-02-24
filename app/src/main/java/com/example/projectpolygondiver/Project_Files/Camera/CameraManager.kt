package com.example.projectpolygondiver.Managers

import android.content.Context
import android.opengl.Matrix
import android.util.DisplayMetrics
import android.util.Log
import com.example.projectpolygondiver.GameObjects.GameObject
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.tan

object CameraManager {
    // Camera properties
    private const val DEFAULT_CAMERA_DISTANCE = 10f
    private const val DEFAULT_FOV = 60f // Field of View in degrees
    private var aspectRatio: Float = 1.0f // Will be set dynamically

    val cameraPosition = Vector3f(0f, 0f, DEFAULT_CAMERA_DISTANCE)
    val lookAtTarget = Vector3f(0f, 0f, 0f)
    private val upVector = Vector3f(0f, 1f, 0f)

    val viewMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    var backgroundGO :GameObject ? = null

    private val scrollSpeed = Vector2f(0.1f, 0.1f)
    // Update view matrix
    fun updateViewMatrix() {
        Matrix.setLookAtM(
            viewMatrix, 0,
            cameraPosition.x, cameraPosition.y, cameraPosition.z,
            lookAtTarget.x, lookAtTarget.y, lookAtTarget.z,
            upVector.x, upVector.y, upVector.z
        )
    }


    fun move(direction: Vector3f) {
        cameraPosition.add(direction)
        lookAtTarget.add(direction)
        updateViewMatrix() // Just update the view matrix for rendering
    }



    // Reset camera position
    fun resetCamera() {
        cameraPosition.set(0f, 0f, DEFAULT_CAMERA_DISTANCE)
        lookAtTarget.set(0f, 0f, 0f)
        updateViewMatrix()
    }

    fun updateProjectionMatrix(width: Int, height: Int) {
        aspectRatio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, DEFAULT_FOV, aspectRatio, 1f, 20f)
        Log.d("CameraDebug", "Projection Matrix Updated -> Aspect Ratio: $aspectRatio")
    }

    // Dynamically scale background to fit screen size using FOV and aspect ratio
    fun getBackgroundScale(screenWidth: Int, screenHeight: Int): Vector3f {
        val cameraDistance = DEFAULT_CAMERA_DISTANCE
        val fovRadians = Math.toRadians(DEFAULT_FOV.toDouble()).toFloat()

        // Convert screen dimensions to OpenGL units
        val heightInWorldUnits  = (2 * tan((fovRadians / 2).toDouble()) * cameraDistance).toFloat() *2f
        val widthInWorldUnits = heightInWorldUnits * aspectRatio

        //Log.d("CameraDebug", "Background Scale -> Width: $widthInWorldUnits, Height: $heightInWorldUnits")
        return Vector3f(widthInWorldUnits, heightInWorldUnits, 1f)
    }

    // Retrieve actual screen dimensions
    fun getScreenDimensions(context: Context): Pair<Int, Int> {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        InputManager.setScreenDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels)
        return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    // Scroll the background texture
    fun scrollBackgroundTexture(dx: Float, dy: Float) {
        backgroundGO?.let {
            // Invert X-axis scrolling if needed
            it.textureOffset.x += dx * scrollSpeed.x * GameObjectManager.deltaTime
            it.textureOffset.y += dy * scrollSpeed.y * GameObjectManager.deltaTime
        }
    }

}
