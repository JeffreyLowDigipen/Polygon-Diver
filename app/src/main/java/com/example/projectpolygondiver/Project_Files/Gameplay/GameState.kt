package com.example.projectpolygondiver.Project_Files.Gameplay

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.example.projectpolygondiver.GameObjects.Player
import com.example.projectpolygondiver.OpenGLActivity.OpenGLES20Activity
import com.example.projectpolygondiver.PlayerScore
import com.example.projectpolygondiver.PlayerScoreDatabase
import com.example.projectpolygondiver.Project_Files.MainActivity
import com.example.projectpolygondiver.R

class GameState : AppCompatActivity()
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
        val intent = Intent(this, OpenGLES20Activity::class.java)
        startActivity(intent)
        Log.d("OpenGL", "OpenGL Activity launched successfully.")
        //finish()
    }

    // Function to simulate a key press safely
    fun simulateKeyPress(keyCode: Int) {
        if (this != null) {
            val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)

            this.dispatchKeyEvent(downEvent)
            this.dispatchKeyEvent(upEvent)
        } else {
            Log.e("InputManager", "Activity reference lost. Cannot simulate key press.")
        }
    }

    private fun showNameInputDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_name, null)
        val editText = dialogView.findViewById<EditText>(R.id.name_input)

        AlertDialog.Builder(this)
            .setTitle("Enter Your Name")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val playerName = editText.text.toString()
                if (playerName.isNotEmpty()) {
                    saveNameToDatabase(playerName)
                } else {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveNameToDatabase(name: String) {
        val newScore = PlayerScore(playerName = name, score = player.score) // Random score
        database.playerScoreDao().insertScore(newScore) // Inserts into Room database
        Toast.makeText(this, "Name saved!", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun GameManager(GameState: GameState)
{
    GameState.start()
}