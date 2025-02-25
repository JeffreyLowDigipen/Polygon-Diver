package com.example.projectpolygondiver.Graphics

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.projectpolygondiver.Managers.*
import com.example.projectpolygondiver.GameObjects.GameObject
import com.example.projectpolygondiver.Managers.CameraManager.backgroundGO
import com.example.projectpolygondiver.Managers.CameraManager.cameraPosition
import java.io.BufferedReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var objLoader: OBJLoader
    private var program: Int = 0

    // MVP Matrices
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var lastFrameTime: Long = System.nanoTime()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
       // GLES30.glEnable(GLES30.GL_CULL_FACE)
      //  GLES30.glDisable(GLES30.GL_CULL_FACE) // Disable backface culling
        GLES30.glFrontFace(GLES30.GL_CCW) // Counter-clockwise winding order
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        // Initialize shaders and program
        val vertexShaderCode = loadShaderFromAssets("Shaders/default_vertex_shader.glsl")
        val fragmentShaderCode = loadShaderFromAssets("Shaders/default_fragment_shader.glsl")
        program = loadShaderProgram(vertexShaderCode, fragmentShaderCode)

        // Load textures AFTER OpenGL context is ready
        objLoader = OBJLoader(context)
        objLoader.Init()

        CameraManager.updateViewMatrix()
        Log.d("MyGLRenderer", "OpenGL context initialized and textures loaded.")
    }


    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glUseProgram(program)

        val currentTime = System.currentTimeMillis()

        var deltaTime = (currentTime - lastFrameTime) / 1000f
        if (deltaTime < 0)
            deltaTime = 0f// Convert nanoseconds to seconds
        lastFrameTime = currentTime

        GameObjectManager.deltaTime = deltaTime;

        GameObjectManager.PreUpdateAddGO()
        //      Log.d("MainActivity","Test")
        InputManager.update(deltaTime);


        //Log.d ("Player" , "Player Pos: ${GameObjectManager.Player?.position?.x},${GameObjectManager.Player?.position?.y},${GameObjectManager.Player?.position?.z}")
        // Log.d("CameraDebug", "background Position -> x: ${backgroundGO?.position?.x}, y: ${backgroundGO?.position?.y}, z: ${backgroundGO?.position?.z}")
        // Log.d("CameraDebug", "Camera Position -> x: ${CameraManager.cameraPosition?.x}, y: ${CameraManager.cameraPosition?.y}, z: ${CameraManager.cameraPosition?.z}")
        //Log.d("CameraDebug", "scale  -> x: ${backgroundGO?.scale?.x}, y: ${backgroundGO?.scale?.y}, z: ${backgroundGO?.scale?.z}")
        // Loop through all game objects and render them
        GameObjectManager.update(deltaTime);
        CameraManager.updateViewMatrix()
        if (!GameObjectManager.pauseGame){
            for (gameObject in GameObjectManager.getAllGameObjects()) {

                gameObject.update(deltaTime)
            }
    }

         GameObjectManager.checkCollisions()

        for (gameObject in GameObjectManager.getAllGameObjects()) {
            renderGameObject(gameObject)
        }
        //Log.d ("GameObjectManager", "${GameObjectManager.getAllGameObjects().size}")
        GameObjectManager.removeGameObjectOnPostUpdate();

        // Update FPS counter
        GameObjectManager.frameCount++
        if (currentTime - GameObjectManager.lastTime >= 1000) {
            GameObjectManager.frameCount = 0
            GameObjectManager.lastTime = currentTime
        }

    }

    // Function to render a game object using its model and texture or color
    private fun renderGameObject(gameObject: GameObject) {

        if (!gameObject.active || !gameObject.renderActive) return

        // Retrieve model and texture from OBJLoader cache
        val vertices = OBJLoader.modelVertices[gameObject.modelName]
        val textureCoords = OBJLoader.modelTextureCoords[gameObject.modelName]
        val textureId = if (gameObject.textureName.isNotEmpty()) {
            OBJLoader.loadedTextures[gameObject.textureName]  // Fallback to 1 if not found
        } else {
            1 // Default texture ID if no texture name is provided
        }


        if (vertices == null || textureCoords == null) {
            Log.e("Renderer", "Missing model or texture data for ${gameObject.modelName}")
            return
        }



        val floatArray = FloatArray(16)
        // Apply camera view and projection matrix
        Matrix.multiplyMM(mvpMatrix, 0, CameraManager.viewMatrix, 0, gameObject.modelMatrix.get(floatArray), 0)
        Matrix.multiplyMM(mvpMatrix, 0, CameraManager.projectionMatrix, 0, mvpMatrix, 0)

        // Pass MVP matrix to shader
        val mvpMatrixHandle = GLES30.glGetUniformLocation(program, "u_MVPMatrix")
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Pass vertex data
        val positionHandle =0
        GLES30.glEnableVertexAttribArray(positionHandle)
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, vertices)

        // Pass texture coordinates
        val texCoordHandle = 1
        if (texCoordHandle != -1) {
            GLES30.glEnableVertexAttribArray(texCoordHandle)
            GLES30.glVertexAttribPointer(texCoordHandle, 2, GLES30.GL_FLOAT, false, 0, textureCoords)
        } else {
            Log.e("OpenGL", "Texture coordinate attribute not found in shader.")
        }


        // Pass light properties
        val lightPosHandle = GLES30.glGetUniformLocation(program, "u_LightPos")
        val lightColorHandle = GLES30.glGetUniformLocation(program, "u_LightColor")
        val viewPosHandle = GLES30.glGetUniformLocation(program, "u_ViewPos")

        GLES30.glUniform3f(lightPosHandle, 0f, 10f, -10f) // Light coming from above and front
        GLES30.glUniform3f(lightColorHandle, 1f, 1f, 1f) // White light
        GLES30.glUniform3f(viewPosHandle, cameraPosition.x, cameraPosition.y, cameraPosition.z) // Camera position

        // Use the GameObject's already updated modelMatrix
        val modelMatrix = gameObject.modelMatrix.get(FloatArray(16))

        // Calculate the normal matrix (inverse transpose of the model matrix)
        val invertedModelMatrix = FloatArray(16)
        Matrix.invertM(invertedModelMatrix, 0, modelMatrix, 0)
        Matrix.transposeM(invertedModelMatrix, 0, invertedModelMatrix, 0)

        // Convert 4x4 matrix to 3x3 for normals
        val normalMatrix = FloatArray(9)
        for (i in 0..2) {
            for (j in 0..2) {
                normalMatrix[i * 3 + j] = invertedModelMatrix[i * 4 + j]
            }
        }


        // Upload the matrices to the shader
        val modelMatrixHandle = GLES30.glGetUniformLocation(program, "u_ModelMatrix")
        GLES30.glUniformMatrix4fv(modelMatrixHandle, 1, false, gameObject.modelMatrix.get(FloatArray(16)), 0)

        val normalMatrixHandle = GLES30.glGetUniformLocation(program, "u_NormalMatrix")
        GLES30.glUniformMatrix3fv(normalMatrixHandle, 1, false, normalMatrix, 0)

        // Pass normal attribute from OBJLoader
        val normalHandle = 2 // Assuming location 2 for normals
        val normals = OBJLoader.modelNormals[gameObject.modelName]
        if (normals != null) {
            GLES30.glEnableVertexAttribArray(normalHandle)
            GLES30.glVertexAttribPointer(normalHandle, 3, GLES30.GL_FLOAT, false, 0, normals)
        }

        // Pass alpha value for solid color rendering
        val alphaHandle = GLES30.glGetUniformLocation(program, "u_Alpha")
        GLES30.glUniform1f(alphaHandle, 0.5f) // 50% transparency


        // Set tint color (e.g., light red tint)
        val tintColorHandle = GLES30.glGetUniformLocation(program, "u_Color")
        GLES30.glUniform3f(tintColorHandle, gameObject.color.x,gameObject.color.y,gameObject.color.z) // Red tint

        // Set tint strength (e.g., 0.3 for light tint)
        val tintStrengthHandle = GLES30.glGetUniformLocation(program, "u_TintStrength")
        GLES30.glUniform1f(tintStrengthHandle, 0.3f)

        // Pass texture offset for scrolling
        val textureOffsetHandle = GLES30.glGetUniformLocation(program, "u_TextureOffset")
        GLES30.glUniform2f(textureOffsetHandle, gameObject.textureOffset.x, gameObject.textureOffset.y)
        //Log.d("OpenGL", "Texture offset: ${gameObject.textureOffset.x} , ${gameObject.textureOffset.y}")
        // Pass color or bind texture
        val useTextureHandle = GLES30.glGetUniformLocation(program, "u_UseTexture")
        if (gameObject.textureName.isNotEmpty()) {
            // Bind the texture
            val textureHandle = GLES30.glGetUniformLocation(program, "u_Texture")
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
            if (textureId != null) {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
            }
            GLES30.glUniform1i(textureHandle, 0)

            // Inform shader to use texture
            GLES30.glUniform1i(useTextureHandle, 1)

          //  Log.d("TextureDebug", "${gameObject.textureName} Texture ID: $textureId")

        } else {
            // Pass color to the shader
            val colorHandle = GLES30.glGetUniformLocation(program, "u_Color")
            GLES30.glUniform3f(colorHandle, gameObject.color.x, gameObject.color.y, gameObject.color.z)

            // Inform shader not to use texture
            GLES30.glUniform1i(useTextureHandle, 0)
        }

        // Draw the model
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertices.capacity() / 3)

        // Disable vertex attributes
        GLES30.glDisableVertexAttribArray(positionHandle)
        GLES30.glDisableVertexAttribArray(texCoordHandle)
    }



    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        CameraManager.updateProjectionMatrix(width, height)
        Log.d("MyGLRenderer", "onSurfaceChanged called with width: $width, height: $height")
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
