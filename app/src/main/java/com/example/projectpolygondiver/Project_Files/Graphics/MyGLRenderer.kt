package com.example.projectpolygondiver.Project_Files.Graphics

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.io.BufferedReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var objLoader: OBJLoader
    private var program: Int = 0

    // MVP Matrices
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var rotationAngle = 0f
    var useTexture = false  // Toggle texture usage

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glDisable(GLES30.GL_CULL_FACE)

        // Load shaders from assets
        val vertexShaderCode = loadShaderFromAssets("Shaders/default_vertex_shader.glsl")
        val fragmentShaderCode = loadShaderFromAssets("Shaders/default_fragment_shader.glsl")

        // Compile and link shaders
        program = loadShaderProgram(vertexShaderCode, fragmentShaderCode)

        // Initialize and load OBJ model
        objLoader = OBJLoader(context, "Models/Chicken_Obj.obj")
        objLoader.load()

        Log.d("MyGLRenderer", "Cat model loaded successfully.")

        // Set up camera view matrix
        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 0f, 4f,  // Camera position
            0f, 0f, 0f,  // Look at center
            0f, 1f, 0f   // Up vector
        )

        val defaultTextureId = IntArray(1)
        GLES30.glGenTextures(1, defaultTextureId, 0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, defaultTextureId[0])
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)

    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glUseProgram(program)
        //Log.d("Test","Draw Start")
        if (!::objLoader.isInitialized || objLoader.vertices.capacity() == 0 || objLoader.textureCoords.capacity() == 0) {
            Log.e("MyGLRenderer", "OBJLoader not initialized or buffer is empty")
            return
        }


        // Auto-center and scale the model based on its bounds
        val center = objLoader.getCenter()
        val scaleFactor = 2f / maxOf(
            objLoader.maxX - objLoader.minX,
            objLoader.maxY - objLoader.minY,
            objLoader.maxZ - objLoader.minZ
        )

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, 0f) // Center the model
        Matrix.scaleM(modelMatrix, 0, scaleFactor, scaleFactor, scaleFactor)
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 0f, 1f, 0f)

        // Compute the MVP matrix
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        // Pass MVP matrix to shader
        val mvpMatrixHandle = GLES30.glGetUniformLocation(program, "u_MVPMatrix")
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        val positionHandle = GLES30.glGetAttribLocation(program, "a_Position")
        if (positionHandle != -1) {
            GLES30.glEnableVertexAttribArray(positionHandle)
            GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, objLoader.vertices)
        } else {
            Log.e("OpenGL_Error", "Position attribute not found in shader.")
        }

        val texCoordHandle = GLES30.glGetAttribLocation(program, "a_TexCoord")
        if (texCoordHandle != -1) {
            GLES30.glEnableVertexAttribArray(texCoordHandle)
            GLES30.glVertexAttribPointer(texCoordHandle, 2, GLES30.GL_FLOAT, false, 0, objLoader.textureCoords)
        } else {
            Log.e("OpenGL_Error", "Texture coordinate attribute not found in shader.")
        }



        // Always pass texture coordinates
        //val texCoordHandle = GLES30.glGetAttribLocation(program, "a_TexCoord")
        GLES30.glEnableVertexAttribArray(texCoordHandle)
        GLES30.glVertexAttribPointer(
            texCoordHandle,
            2,
            GLES30.GL_FLOAT,
            false,
            0,
            objLoader.textureCoords
        )


        // Handle texture binding
        if (useTexture) {
            val textureHandle = GLES30.glGetUniformLocation(program, "u_Texture")
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, objLoader.textureId)
            GLES30.glUniform1i(textureHandle, 0)
        }
         else {
            // Bind a default empty texture to avoid crashes
            var textureHandle = GLES30.glGetUniformLocation(program, "u_Texture")
           // Log.d ("OpenGL_Error" ,"Texture ID: $textureHandle")
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            GLES30.glUniform1i(textureHandle, 0)

            textureHandle = GLES30.glGetUniformLocation(program, "u_Texture")
            Log.d ("OpenGL_Error" ,"Texture ID: $textureHandle")
        }

        val error = GLES30.glGetError()
        if (error != GLES30.GL_NO_ERROR) {
            Log.e("OpenGL_Error", "Error after binding texture: $error")
        }


        val vertexCount = objLoader.vertices.capacity() / 3
        val textureCoordCount = objLoader.textureCoords.capacity() / 2

       // if (vertexCount <= 0 || textureCoordCount <= 0) {
        Log.d("GLDrawError", "Invalid vertex or texture coordinate count: Vertices = $vertexCount, Texture Coords = $textureCoordCount")
            //return
       // }



        // Draw the model
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, objLoader.vertices.capacity() / 3)

        if (positionHandle != -1) GLES30.glDisableVertexAttribArray(positionHandle)
        if (texCoordHandle != -1) GLES30.glDisableVertexAttribArray(texCoordHandle)


        // Increment rotation
        rotationAngle += 0.5f

       // Log.d("Test","Draw End")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val aspectRatio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 60f, aspectRatio, 1f, 20f)
    }

    // Load shader from assets folder
    private fun loadShaderFromAssets(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use(BufferedReader::readText)
    }

    // Compile and link shaders
    private fun loadShaderProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
        val vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)

        return GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)

            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(it, GLES30.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                Log.e("ShaderError", "Program Linking Failed: ${GLES30.glGetProgramInfoLog(it)}")
                GLES30.glDeleteProgram(it)
            }
        }
    }

    // Compile shader with error checking
    private fun compileShader(type: Int, code: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, code)
        GLES30.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val errorMsg = GLES30.glGetShaderInfoLog(shader)
            Log.e("ShaderError", "Shader Compilation Failed: $errorMsg")
            GLES30.glDeleteShader(shader)
        }
        return shader
    }
}
