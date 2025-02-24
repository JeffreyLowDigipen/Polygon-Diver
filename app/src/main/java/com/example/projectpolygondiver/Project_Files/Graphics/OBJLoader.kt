package com.example.projectpolygondiver.Graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class OBJLoader(private val context: Context) {

    lateinit var vertices: FloatBuffer
    lateinit var textureCoords: FloatBuffer

    // Global caches for model data and textures
    companion object {
        val modelVertices = mutableMapOf<String, FloatBuffer>()
        val modelTextureCoords = mutableMapOf<String, FloatBuffer>()
        val loadedTextures = mutableMapOf<String, Int>() // Cache textures using their file path as keys
    }


    fun Init()
    {
        generatePrimitiveCube()
        generatePrimitivePlane()
       // generatePlainTexture()
       // loadTexture("Textures/Chicken_Tx.jpg")
        listAssetFiles(context, "Textures")  // Lists files inside the Textures folder
        loadTexture("Textures/background.jpg")
        loadTexture("Textures/Chicken_Tx.jpg")

    }
    // Load a model and cache data
    fun loadModel(modelName: String) {
        // Check if model data is already cached
        if (modelVertices.containsKey(modelName.trim())) {
            Log.d("OBJLoader", "Model data loaded from cache: $modelName")
            vertices = modelVertices[modelName]!!
            textureCoords = modelTextureCoords[modelName]!!
            return
        }

        // Initialize local lists
        val vertexList = mutableListOf<Float>()
        val textureList = mutableListOf<Float>()
        val faceList = mutableListOf<Int>()
        val texCoordList = mutableListOf<Int>()

        // Load model from assets
        val inputStream = context.assets.open(modelName)
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.forEachLine { line ->
            val parts = line.trim().split("\\s+".toRegex())
            when (parts[0]) {
                "v" -> { // Vertex positions
                    val x = parts[1].toFloat()
                    val y = parts[2].toFloat()
                    val z = parts[3].toFloat()
                    vertexList.addAll(listOf(x, y, z))
                }

                "vt" -> { // Texture coordinates
                    if (parts.size >= 3) {
                        textureList.add(parts[1].toFloat())
                        textureList.add(1.0f - parts[2].toFloat())
                    } else {
                        textureList.add(0.0f)
                        textureList.add(0.0f)
                    }
                }

                "f" -> { // Faces
                    if (parts.size == 4) {
                        for (i in 1..3) {
                            val indices = parts[i].split("/")
                            faceList.add(indices[0].toInt() - 1)
                            texCoordList.add(if (indices.size > 1 && indices[1].isNotEmpty()) indices[1].toInt() - 1 else 0)
                        }
                    }
                }
            }
        }

        if (vertexList.isEmpty() || faceList.isEmpty()) {
            Log.e("OBJLoader", "Model loading failed: Empty vertex or face list")
            return
        }

        initializeBuffers(modelName, vertexList, textureList, faceList, texCoordList)
    }

    // Initialize vertex and texture buffers and cache them
    private fun initializeBuffers(
        modelName: String,
        vertexList: List<Float>,
        textureList: List<Float>,
        faceList: List<Int>,
        texCoordList: List<Int>
    ) {
        val vertexData = FloatArray(faceList.size * 3)
        for (i in faceList.indices) {
            val vertexIndex = faceList[i] * 3
            vertexData[i * 3] = vertexList[vertexIndex]
            vertexData[i * 3 + 1] = vertexList[vertexIndex + 1]
            vertexData[i * 3 + 2] = vertexList[vertexIndex + 2]
        }

        vertices = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertices.put(vertexData).position(0)

        val textureData = FloatArray(texCoordList.size * 2)
        for (i in texCoordList.indices) {
            val texIndex = texCoordList[i] * 2
            textureData[i * 2] = textureList[texIndex]
            textureData[i * 2 + 1] = textureList[texIndex + 1]
        }

        textureCoords = ByteBuffer.allocateDirect(textureData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureCoords.put(textureData).position(0)

        // Remove directory path and file extension from model name
        val trimmedModelName = modelName.substringAfterLast("/").substringBeforeLast(".")

        modelVertices[trimmedModelName] = vertices
        modelTextureCoords[trimmedModelName] = textureCoords

        Log.d("OBJLoader", "Model loaded and cached: $trimmedModelName")



    }

    // Load texture and cache it globally
    fun loadTexture(textureFile: String): Int {

        if (GLES30.glGetError() != GLES30.GL_NO_ERROR) {
            Log.e("TextureLoader", "OpenGL error before generating texture")
        }

        // Check if texture is already cached
        if (loadedTextures.containsKey(textureFile)) {
            Log.d("TextureLoader", "Texture loaded from cache: $textureFile at ${loadedTextures[textureFile]}")
            return loadedTextures[textureFile]!!
        }

        // Generate unique texture ID
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)

        // Check if texture generation failed
        if (textureIds[0] == 0) {
            Log.e("TextureLoader", "Failed to generate texture ID for $textureFile")
            return 0
        }

        // Decode the bitmap from the asset folder
        val bitmap = BitmapFactory.decodeStream(context.assets.open(textureFile))
        val flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply {
            preScale(1f, -1f) // Flip vertically for OpenGL
        }, true)

        // Bind the texture and set its parameters
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, flippedBitmap, 0)
        flippedBitmap.recycle()

        val trimmedTextureName = textureFile.substringAfterLast("/").substringBeforeLast(".")
        // Store the texture in the cache with its ID
        loadedTextures[trimmedTextureName] = textureIds[0]

        // Log for debugging
        Log.d("TextureLoader", "Texture loaded and cached: $trimmedTextureName at ${textureIds[0]}")

        return textureIds[0]
    }


    // Load sprite texture for 2D rendering
    fun loadSpriteTexture(textureFile: String): Int {
        if (loadedTextures.containsKey(textureFile)) {
            Log.d("OBJLoader", "Sprite texture loaded from cache: $textureFile")
            return loadedTextures[textureFile]!!
        }

        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)

        val bitmap = BitmapFactory.decodeStream(context.assets.open(textureFile))
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])

        // Texture parameters optimized for sprites
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        loadedTextures[textureFile] = textureIds[0]
        Log.d("OBJLoader", "Sprite texture loaded and cached: $textureFile")

        return textureIds[0]
    }

    fun generatePrimitivePlane() {
        if (modelVertices.containsKey("plane")) {
            Log.d("OBJLoader", "Primitive model already generated: plane")
            return
        }

        // Define a square in 3D space for sprite rendering
        val halfSize = 1f / 2f
        val vertexData = floatArrayOf(
            -halfSize, halfSize, 0f,  // Top-left
            halfSize, halfSize, 0f,   // Top-right
            -halfSize, -halfSize, 0f, // Bottom-left
            halfSize, -halfSize, 0f   // Bottom-right
        )

        val textureData = floatArrayOf(
            0f, 0f,  // Top-left
            1f, 0f,  // Top-right
            0f, 1f,  // Bottom-left
            1f, 1f   // Bottom-right
        )

        // Define indices for two triangles
        val indices = intArrayOf(
            0, 1, 2,  // First triangle (Top-left, Top-right, Bottom-left)
            1, 3, 2   // Second triangle (Top-right, Bottom-right, Bottom-left)
        )

        // Convert arrays into buffers
        val finalVertexData = FloatArray(indices.size * 3)
        val finalTextureData = FloatArray(indices.size * 2)

        for (i in indices.indices) {
            val vertexIndex = indices[i] * 3
            finalVertexData[i * 3] = vertexData[vertexIndex]
            finalVertexData[i * 3 + 1] = vertexData[vertexIndex + 1]
            finalVertexData[i * 3 + 2] = vertexData[vertexIndex + 2]

            val texIndex = indices[i] * 2
            finalTextureData[i * 2] = textureData[texIndex]
            finalTextureData[i * 2 + 1] = textureData[texIndex + 1]
        }

        vertices = ByteBuffer.allocateDirect(finalVertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertices.put(finalVertexData).position(0)

        textureCoords = ByteBuffer.allocateDirect(finalTextureData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureCoords.put(finalTextureData).position(0)

        // Cache the primitive model
        modelVertices["plane"] = vertices
        modelTextureCoords["plane"] = textureCoords

        Log.d("OBJLoader", "Primitive model generated and cached: plane")
    }

    fun generatePrimitiveCube() {
        if (modelVertices.containsKey("cube")) {
            Log.d("OBJLoader", "Primitive cube already generated: cube")
            return
        }

        val halfSize = 1f / 2f
        val vertexData = floatArrayOf(
            // Front face
            -halfSize, -halfSize,  halfSize, // Bottom-left
            halfSize, -halfSize,  halfSize, // Bottom-right
            halfSize,  halfSize,  halfSize, // Top-right
            -halfSize,  halfSize,  halfSize, // Top-left

            // Back face
            -halfSize, -halfSize, -halfSize,
            halfSize, -halfSize, -halfSize,
            halfSize,  halfSize, -halfSize,
            -halfSize,  halfSize, -halfSize
        )

        val textureData = floatArrayOf(
            // Front face
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f,
            // Back face
            0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f
        )

        // Define the indices for drawing triangles (two per face)
        val indices = intArrayOf(
            // Front face
            0, 1, 2, 0, 2, 3,
            // Back face
            4, 5, 6, 4, 6, 7,
            // Left face
            4, 0, 3, 4, 3, 7,
            // Right face
            1, 5, 6, 1, 6, 2,
            // Top face
            3, 2, 6, 3, 6, 7,
            // Bottom face
            4, 5, 1, 4, 1, 0
        )

        // Create buffers for vertices and texture coordinates
        val finalVertexData = FloatArray(indices.size * 3)
        val finalTextureData = FloatArray(indices.size * 2)

        for (i in indices.indices) {
            val vertexIndex = indices[i] * 3
            finalVertexData[i * 3] = vertexData[vertexIndex]
            finalVertexData[i * 3 + 1] = vertexData[vertexIndex + 1]
            finalVertexData[i * 3 + 2] = vertexData[vertexIndex + 2]

            val texIndex = indices[i] * 2
            finalTextureData[i * 2] = textureData[texIndex]
            finalTextureData[i * 2 + 1] = textureData[texIndex + 1]
        }

        // Convert arrays into buffers
        vertices = ByteBuffer.allocateDirect(finalVertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertices.put(finalVertexData).position(0)

        textureCoords = ByteBuffer.allocateDirect(finalTextureData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureCoords.put(finalTextureData).position(0)

        modelVertices["cube"] = vertices
        modelTextureCoords["cube"] = textureCoords

        Log.d("OBJLoader", "Cube primitive generated and cached: cube")
    }
    // Function to generate a plain color texture and cache it
    fun generatePlainTexture(): Int {
        if (loadedTextures.containsKey("plain")) {
            Log.d("OBJLoader", "Plain color texture loaded from cache: plain")
            return loadedTextures["plain"] !!
        }

        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)

        // Convert color from Vector3f (values between 0 and 1) to ARGB format
        val red = 0
        val green = 0
        val blue = 0
        val alpha = 255

        val pixelColor = Color.argb(alpha, red, green, blue)
        val buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
        buffer.put(pixelColor).position(0)

        // Bind texture and set parameters
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1, 1, 0,
            GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buffer
        )

        loadedTextures["plain"] = textureIds[0]
        Log.d("OBJLoader", "Plain color texture generated and cached: plain")

        return textureIds[0]
    }
    fun listAssetFiles(context: Context, path: String) {
        try {
            val assetManager = context.assets
            val files = assetManager.list(path) // List files in the given folder

            if (files != null && files.isNotEmpty()) {
                Log.d("AssetChecker", "Files in $path:")
                for (file in files) {
                    Log.d("AssetChecker", file)
                }
            } else {
                Log.d("AssetChecker", "No files found in $path.")
            }
        } catch (e: Exception) {
            Log.e("AssetChecker", "Error accessing assets in $path: ${e.message}")
        }
    }

}

