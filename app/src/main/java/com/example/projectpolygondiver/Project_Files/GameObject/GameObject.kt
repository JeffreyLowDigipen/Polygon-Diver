package com.example.projectpolygondiver.GameObjects

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

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


    open fun render()
    {

    }

}
