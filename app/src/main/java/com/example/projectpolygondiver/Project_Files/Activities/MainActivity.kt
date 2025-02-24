package com.example.projectpolygondiver.Project_Files

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.projectpolygondiver.GameObjects.GameObject
import com.example.projectpolygondiver.GameObjects.Player
import com.example.projectpolygondiver.Graphics.*
import com.example.projectpolygondiver.Managers.*
import org.joml.Vector3f
import kotlinx.coroutines.CompletableDeferred
import com.example.projectpolygondiver.OpenGLActivity.*


class MainActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 1001

    // Deferred variable to wait for OpenGL context initialization
    private val openGLInitialized = CompletableDeferred<Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Launch OPENGL")

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

            initializeGameAssets()

    }

    fun initializeGameAssets() {

       // val objLoader = OBJLoader(this@MainActivity)
        //objLoader.Init()
        //  objLoader.generatePrimitiveCube()

        val (screenWidth, screenHeight) = CameraManager.getScreenDimensions(this@MainActivity)
        val backgroundScale = CameraManager.getBackgroundScale(screenWidth, screenHeight)

        Log.d("ScreenDebug", "Screen Width: $screenWidth px, Screen Height: $screenHeight px")
        Log.d(
            "ScaleDebug",
            "Background Scale -> X: ${backgroundScale.x}, Y: ${backgroundScale.y}, Z: ${backgroundScale.z}"
        )

        val player = Player().apply {
            modelName = "cube"
            position = Vector3f(0f, 0f, 0f)
            scale = Vector3f(0.4f, 0.6f, 0.4f)
            color = Vector3f(1f, 1f, 1f)
            //textureName="Chicken_Tx"
            type = GameObject.GOType.PLAYER
            movementSpeed=2f
        }

        val Enemy = GameObject().apply {
            modelName = "plane"
            position = Vector3f(0f, -5f, 0f)
            scale = Vector3f(0.4f, 0.6f, 0.4f)
            color = Vector3f(1f, 1f, 1f)
            type = GameObject.GOType.ENEMY
            movementSpeed=2f
        }

        val background = GameObject().apply {
            modelName = "plane"
            position = Vector3f(0f, 0f, -1f)
            scale = backgroundScale.mul(50f);
            color = Vector3f(0.01f, 0.01f, 0.01f)
            textureName = "background"
            type = GameObject.GOType.DEFAULT
        }

        CameraManager.backgroundGO = background


        GameObjectManager.addGameObject(player)
        GameObjectManager.addGameObject(Enemy)
        GameObjectManager.Player = player
        GameObjectManager.addGameObject(background)

        Log.d("MainActivity", "Assets loaded and objects added successfully.")
        launchOpenGLActivity()
    }

    private fun launchOpenGLActivity() {
        val intent = Intent(this, OpenGLES20Activity::class.java)
        startActivity(intent)
        finish()
    }
}



