package com.example.projectpolygondiver.GameObjects

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import java.lang.Math.toDegrees
import kotlin.math.asin
import kotlin.math.atan2

open class GameObject {

    enum class GOType
    {
       DEFAULT,
       PLAYER,
        BULLET,
       ENEMY,
    }

    // Transform properties
    var position: Vector3f = Vector3f(0f, 0f, 0f)
    var rotation: Vector3f = Vector3f(0f, 0f, 0f) // Rotation in degrees (pitch, yaw, roll)
    var scale: Vector3f = Vector3f(1f, 1f, 1f)
    var color : Vector3f = Vector3f(1f,1f,1f)
    // Transformation matrix
    val modelMatrix: Matrix4f = Matrix4f()
    var modelName : String ="";
    var textureName : String = "";
    var active : Boolean = false;
    var textureOffset: Vector2f = Vector2f(0f, 0f)
    var type : GOType = GOType.DEFAULT
    public var movementSpeed = 10f;
    open fun Init()
    {
        position = Vector3f(0f, 0f, 0f)
        rotation =  Vector3f(0f, 0f, 0f) // Rotation in degrees (pitch, yaw, roll)
        scale =  Vector3f(1f, 1f, 1f)
    }
    // Update called every frame
    open fun update(deltaTime: Float) {
        // Perform object-specific updates here (animations, AI, etc.)
       // rotation.x +=1;
        computeTransformationMatrix()
    }
    // Axis-Aligned Bounding Box (AABB) collision check
    fun isCollidingWith(other: GameObject): Boolean {
        return !(position.x + scale.x / 2 < other.position.x - other.scale.x / 2 ||
                position.x - scale.x / 2 > other.position.x + other.scale.x / 2 ||
                position.y + scale.y / 2 < other.position.y - other.scale.y / 2 ||
                position.y - scale.y / 2 > other.position.y + other.scale.y / 2)
    }

    open fun onCollision(other: GameObject) {
        // Override this in subclasses for custom collision behavior
    }
    // Computes the model matrix based on position, rotation, and scale
    private fun computeTransformationMatrix() {
        modelMatrix.identity() // Reset the matrix



        // Apply transformations in order: Scale → Rotate → Translate
        modelMatrix.translate(position)
            .rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            .rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())
            .scale(scale)
    }



    fun computeTopDownYaw(direction: Vector3f): Float {
        if (direction.length() == 0f) return 0f // Avoid zero-length vector

        val normalizedDirection = direction.normalize()

        // Use X and Z for proper top-down rotation (Y-axis is the vertical axis)
        val yaw = toDegrees(atan2(-normalizedDirection.z.toDouble(), normalizedDirection.x.toDouble())).toFloat()

        // Ensure yaw stays within 0-360 degrees
        return if (yaw < 0) yaw + 360f else yaw
    }
    fun rotateModel(deltaRotation: Float, deltaTime: Float) {
        // Smooth continuous rotation around the Y-axis
        rotation.y += deltaRotation * deltaTime

        // Ensure rotation stays within 0-360 degrees
        if (rotation.y >= 360f) rotation.y -= 360f
        if (rotation.y < 0f) rotation.y += 360f
    }

    fun computeYawFromDirection(direction: Vector3f): Float {
        if (direction.length() == 0f) return rotation.y // Keep current rotation if there's no movement

        val normalizedDirection = direction.normalize()

        // atan2 returns a value between -PI and PI, allowing for smooth 360° rotation
        val yaw = toDegrees(atan2(normalizedDirection.x.toDouble(), normalizedDirection.z.toDouble())).toFloat()

        return if (yaw < 0) yaw + 360f else yaw // Convert negative angles to positive for full rotation
    }



    open fun render()
    {

    }

}
