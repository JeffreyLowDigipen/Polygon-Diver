package com.example.projectpolygondiver.Project_Files.Graphics

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLUtils
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class OBJLoader(private val context: Context, private val objFileName: String) {

    lateinit var vertices: FloatBuffer
    lateinit var textureCoords: FloatBuffer

    private val vertexList = mutableListOf<Float>()
    private val textureList = mutableListOf<Float>()
    private val faceList = mutableListOf<Int>()
    private val texCoordList = mutableListOf<Int>()

    // Bounds for centering and scaling
    var minX = Float.MAX_VALUE
    var minY = Float.MAX_VALUE
    var minZ = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var maxY = Float.MIN_VALUE
    var maxZ = Float.MIN_VALUE

    var textureId: Int = 0
    private var texturePath: String = ""

    var bumpTextureId: Int = -1
    private var bumpTexturePath: String = ""
    var hasBumpMap: Boolean =false
    fun load() {
        val inputStream = context.assets.open(objFileName)
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.forEachLine { line ->
            val parts = line.trim().split("\\s+".toRegex())
            when (parts[0]) {
                "v" -> { // Vertex positions
                    val x = parts[1].toFloat()
                    val y = parts[2].toFloat()
                    val z = parts[3].toFloat()

                    vertexList.add(x)
                    vertexList.add(y)
                    vertexList.add(z)

                    // Update bounds
                    minX = minOf(minX, x)
                    minY = minOf(minY, y)
                    minZ = minOf(minZ, z)
                    maxX = maxOf(maxX, x)
                    maxY = maxOf(maxY, y)
                    maxZ = maxOf(maxZ, z)
                }
                "vt" -> {
                    if (parts.size >= 3) {
                        textureList.add(parts[1].toFloat()) // U-coordinate
                        textureList.add(1.0f - parts[2].toFloat()) // Flip V-coordinate
                    } else {
                        // Add a default texture coordinate if missing
                        textureList.add(0.0f)
                        textureList.add(0.0f)
                    }
                }

                "f" -> {
                    if (parts.size == 5) { // Quad face (4 vertices)
                        val indices = parts.subList(1, 5).map { it.split("/")[0].toInt() - 1 }

                        // Convert quad into two triangles
                        faceList.add(indices[0])
                        faceList.add(indices[1])
                        faceList.add(indices[2])

                        faceList.add(indices[0])
                        faceList.add(indices[2])
                        faceList.add(indices[3])
                    } else if (parts.size == 4) { // Triangle face (3 vertices)
                        for (i in 1..3) { // Preserve CCW winding order
                            val indices = parts[i].split("/")
                            faceList.add(indices[0].toInt() - 1)
                            if (indices.size > 1 && indices[1].isNotEmpty()) {
                                texCoordList.add(indices[1].toInt() - 1)
                            } else {
                                texCoordList.add(0) // Default texture coordinate
                            }
                        }
                    }
                }



                "mtllib" -> { // Load MTL file
                    loadMTL(parts[1])
                }
            }
        }

        if (vertexList.isEmpty() || faceList.isEmpty()) {
            Log.e("OBJLoader", "Model loading failed: Empty vertex or face list")
            return
        }
        if (textureList.isEmpty() || texCoordList.isEmpty()) {
            Log.e("OBJLoader", "Missing texture coordinates, initializing default UVs.")
            for (i in 0 until vertexList.size / 3) {
                textureList.add(0.0f) // Default U-coordinate
                textureList.add(0.0f) // Default V-coordinate
            }

            for (i in 0 until faceList.size) {
                texCoordList.add(0) // Default texture index
            }
        }



        // Log bounds
        Log.d("OBJLoader", "Model Bounds: Min($minX, $minY, $minZ) Max($maxX, $maxY, $maxZ)")

        // Load vertex data into buffer
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




        // Load texture coordinates into buffer
        val textureData = FloatArray(texCoordList.size * 2)
        for (i in texCoordList.indices) {
            val texIndex = texCoordList[i] * 2
            textureData[i * 2] = textureList[texIndex]
            textureData[i * 2 + 1] = textureList[texIndex + 1]
        }

        if (textureList.size / 2 < vertexList.size / 3) {
            val missingUVs = (vertexList.size / 3) - (textureList.size / 2)
            for (i in 0 until missingUVs) {
                textureList.add(0.0f) // Default U-coordinate
                textureList.add(0.0f) // Default V-coordinate
            }
            Log.e("OBJLoader", "Added $missingUVs default texture coordinates for missing UVs.")
        }



        textureCoords = ByteBuffer.allocateDirect(textureData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureCoords.put(textureData).position(0)

        // Load texture from file
        if (texturePath.isNotEmpty()) {
            textureId = loadTexture(texturePath)
        }
        if (bumpTexturePath.isNotEmpty()) {
            bumpTextureId = loadTexture(bumpTexturePath)
            hasBumpMap=true
        }
        Log.d("OBJLoader", "Vertex Buffer Capacity: ${vertices.capacity() / 3}")
        Log.d("OBJLoader", "Texture Coord Buffer Capacity: ${textureCoords.capacity() / 2}")


        initializeTexture()
    }

    // Load the texture from MTL file
    private fun loadMTL(mtlFileName: String) {
        try {
            val inputStream = context.assets.open("Models/$mtlFileName")
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.forEachLine { line ->
                val parts = line.trim().split("\\s+".toRegex())
                when (parts[0]) {
                    "map_Kd" -> { // Diffuse texture
                        texturePath = "Textures/${parts[1]}"
                        Log.d("OBJLoader", "Adjusted Texture Path: $texturePath")
                    }
                    "map_bump", "bump" -> { // Bump map texture
                        bumpTexturePath = "Textures/${parts[1]}"
                        Log.d("OBJLoader", "Bump Texture Loaded: $bumpTexturePath")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("OBJLoader", "Failed to load MTL file: $mtlFileName, Error: ${e.message}")
        }
    }


    // Load the texture into OpenGL
    private fun loadTexture(textureFile: String): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)
       // if (textureIds[0] == 0) {
            Log.d("TextureLoad", "Generate texture ID. ${textureIds[0]}")
          //  return -1
        //}
        val bitmap = BitmapFactory.decodeStream(context.assets.open(textureFile))

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        return textureIds[0]
    }

    // Calculate the center of the model for proper positioning
    fun getCenter(): FloatArray {
        return floatArrayOf(
            (minX + maxX) / 2f,
            (minY + maxY) / 2f,
            (minZ + maxZ) / 2f
        )
    }

    // Calculate scale factor based on the largest dimension
    fun getScaleFactor(): Float {
        val sizeX = maxX - minX
        val sizeY = maxY - minY
        val sizeZ = maxZ - minZ
        return 2f / maxOf(sizeX, sizeY, sizeZ) // Normalize to fit within [-1, 1]
    }

    fun initializeTexture(): Int {
        val textureIds = IntArray(1)
        GLES30.glGenTextures(1, textureIds, 0)

        Log.d("TextureLoad", "Generate texture ID. ${textureIds[0]}")

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0])

        // Set texture parameters
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)

        // Create a 1x1 pixel dummy texture
        val pixel = intArrayOf(0xFFFFFFFF.toInt()) // White color
        val buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
        buffer.put(pixel).position(0)

        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1, 1, 0,
            GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buffer
        )

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        return textureIds[0]
    }

}
