package com.example.projectpolygondiver.Project_Files

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.projectpolygondiver.AppNavHost
import com.example.projectpolygondiver.GameObjects.GameObject
import com.example.projectpolygondiver.GameObjects.Player
import com.example.projectpolygondiver.Managers.*
import com.example.projectpolygondiver.NavViewModel
import org.joml.Vector3f
import kotlinx.coroutines.CompletableDeferred

//import com.example.projectpolygondiver.Project_Files.Gameplay.GameState

class MainActivity : AppCompatActivity() {
    private val STORAGE_PERMISSION_CODE = 1001

    // Deferred variable to wait for OpenGL context initialization
    private val openGLInitialized = CompletableDeferred<Unit>()

    var initialised = false
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Launch OPENGL")

        setContent {
            MaterialTheme {
                val navViewModel: NavViewModel = viewModel()
                navController = rememberNavController()

                AppNavHost(navController = navController, navViewModel = navViewModel)
            }
        }

        handleNavigationIntent(intent)

        // Request permission first
        if (checkStoragePermission()) {
            initializeGame()
        } else {
            requestStoragePermission()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNavigationIntent(intent)
    }

    private fun handleNavigationIntent(intent: Intent?) {
        intent?.let {
            if (it.getBooleanExtra("navigateToMainMenu", false)) {
                // Navigate to the main menu
                navController.navigate("mainMenu") {
                    popUpTo("game") { inclusive = true }
                }
            }
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

//        val (screenWidth, screenHeight) = CameraManager.getScreenDimensions(this@MainActivity)
//      //  val backgroundScale = CameraManager.getBackgroundScale(screenWidth, screenHeight)
//        CameraManager.screenWidth= screenWidth.toFloat()
//        CameraManager.screenHeight= screenHeight.toFloat()
//        Log.d("ScreenDebug", "Screen Width: $screenWidth px, Screen Height: $screenHeight px")
//        Log.d(
//            "ScaleDebug",
//            "Background Scale -> X: ${backgroundScale.x}, Y: ${backgroundScale.y}, Z: ${backgroundScale.z}"
//        )

//        val player = Player().apply {
//            modelName = "plane"
//            position = Vector3f(0f, 0f, 0f)
//            rotation = Vector3f(0f, 0f, 0f)
//            scale = Vector3f(1f, 1f, 1f)
//            color = Vector3f(0f, 0f, 0f)
//            textureName = "microwave"
//            type = GameObject.GOType.PLAYER
//            movementSpeed = 3f
//        }
//
//
//
//        val background = GameObject().apply {
//            modelName = "plane"
//            position = Vector3f(0f, 0f, -1f)
//            scale = backgroundScale.mul(3f);
//            color = Vector3f(0.01f, 0.01f, 0.01f)
//            textureName = "background"
//            type = GameObject.GOType.DEFAULT
//        }

//        CameraManager.backgroundGO = background
//
//        //GameObjectManager.addGameObject(Enemy)
//        GameObjectManager.Player = player
//        GameObjectManager.addGameObject(background)
//        GameObjectManager.addGameObject(player)
        Log.d("MainActivity", "Assets loaded and objects added successfully.")

        initialised = true
        openGLInitialized.complete(Unit)
       // launchOpenGLActivity()
    }
}




