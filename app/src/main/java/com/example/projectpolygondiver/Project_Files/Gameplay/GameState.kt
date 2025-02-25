package com.example.projectpolygondiver.Project_Files.Gameplay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projectpolygondiver.AppNavHost
import com.example.projectpolygondiver.GameObjects.Player
import com.example.projectpolygondiver.MainMenu
import com.example.projectpolygondiver.Managers.GameObjectManager
import com.example.projectpolygondiver.OpenGLActivity.OpenGLES20Activity
import com.example.projectpolygondiver.PlayerScore
import com.example.projectpolygondiver.PlayerScoreDatabase
import com.example.projectpolygondiver.Project_Files.MainActivity
import com.example.projectpolygondiver.R

class GameState(private val activity: Activity, private val navController: NavController)
{
    private lateinit var database: PlayerScoreDatabase

    private var player = Player()

    private var initialiser = MainActivity()

    fun start()
    {
        if(initialiser.initialised)
        {

            launchOpenGLActivity()
        }
        else
        {
            Log.d("GameInit", "Game is still initializing. Retrying in 100ms...")
            Handler(Looper.getMainLooper()).postDelayed({ start() }, 100)
        }
    }

    private fun launchOpenGLActivity() {
        Log.d("OpenGL", "Starting OpenGL Activity...")
        val intent = Intent(activity, OpenGLES20Activity::class.java)

        activity.startActivity(intent)

        Log.d("OpenGL", "OpenGL Activity launched successfully.")
        //finish()
    }

    // Function to simulate a key press safely
    fun simulateKeyPress(keyCode: Int) {
        if (this != null) {
            val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)

            activity.dispatchKeyEvent(downEvent)
            activity.dispatchKeyEvent(upEvent)
        } else {
            Log.e("InputManager", "Activity reference lost. Cannot simulate key press.")
        }
    }
}

@Composable
fun GameManager(navController: NavController, context: Context) {
    val activity = context as Activity

    LaunchedEffect(Unit) {
        Log.d("GameManager", "Launching OpenGL Activity from Composable")
        val intent = Intent(context, OpenGLES20Activity::class.java)
        context.startActivity(intent)
    }
}
