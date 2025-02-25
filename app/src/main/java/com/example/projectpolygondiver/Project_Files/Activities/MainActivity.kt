package com.example.projectpolygondiver.Project_Files

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import com.example.projectpolygondiver.AppNavHost
import com.example.projectpolygondiver.GameObjects.GameObject
import com.example.projectpolygondiver.GameObjects.Player
import com.example.projectpolygondiver.Graphics.*
import com.example.projectpolygondiver.Managers.*
import org.joml.Vector3f
import kotlinx.coroutines.CompletableDeferred
import com.example.projectpolygondiver.OpenGLActivity.*
import com.example.projectpolygondiver.Project_Files.Gameplay.GameState

class MainActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 1001

    // Deferred variable to wait for OpenGL context initialization
    private val openGLInitialized = CompletableDeferred<Unit>()

    var initialised = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Launch OPENGL")

        setContent {
            MaterialTheme {
                AppNavHost()
            }
        }

        // Request permission first
        if (checkStoragePermission()) {
            initializeGame()
        } else {
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            STORAGE_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Storage permission granted")
                initializeGame()
            } else {
                Log.e("Permission", "Storage permission denied")
            }
        }
    }

    private fun initializeGame() {
        if(!initialised)
        {
            initializeGameAssets()
        }
    }

    fun initializeGameAssets() {
        Log.d("GameInit", "Initializing game assets...")
        // val objLoader = OBJLoader(this@MainActivity)
        //objLoader.Init()
        //  objLoader.generatePrimitiveCube()

        if(initialised)
        {
            Log.d("MainActivity", "Already initialised")
            return
        }

        val (screenWidth, screenHeight) = CameraManager.getScreenDimensions(this@MainActivity)
        val backgroundScale = CameraManager.getBackgroundScale(screenWidth, screenHeight)

        Log.d("ScreenDebug", "Screen Width: $screenWidth px, Screen Height: $screenHeight px")
        Log.d(
            "ScaleDebug",
            "Background Scale -> X: ${backgroundScale.x}, Y: ${backgroundScale.y}, Z: ${backgroundScale.z}"
        )

        val player = Player().apply {
            modelName = "plane"
            position = Vector3f(0f, 0f, 0f)
            rotation = Vector3f(0f, 0f, 0f)
            scale = Vector3f(1f, 1f, 1f)
            color = Vector3f(1f, 1f, 1f)
            textureName = "microwave"
            type = GameObject.GOType.PLAYER
            movementSpeed = 3f
        }



        val background = GameObject().apply {
            modelName = "plane"
            position = Vector3f(0f, 0f, -1f)
            scale = backgroundScale.mul(5f);
            color = Vector3f(0.01f, 0.01f, 0.01f)
            textureName = "background"
            type = GameObject.GOType.DEFAULT
        }

        CameraManager.backgroundGO = background


        GameObjectManager.addGameObject(player)
        //GameObjectManager.addGameObject(Enemy)
        GameObjectManager.Player = player
        GameObjectManager.addGameObject(background)

        Log.d("MainActivity", "Assets loaded and objects added successfully.")

        initialised = true
        openGLInitialized.complete(Unit)
        //launchOpenGLActivity()
    }
}




