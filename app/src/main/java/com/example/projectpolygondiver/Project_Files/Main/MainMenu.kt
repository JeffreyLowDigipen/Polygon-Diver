package com.example.projectpolygondiver

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Relation
import com.example.projectpolygondiver.Managers.GameObjectManager
import kotlin.system.exitProcess

@Composable
fun MainMenu(navController: NavController)
{
    val context = LocalContext.current
    val database = PlayerScoreDatabase.getDatabase(context)
    val repository = PlayerScoreRepository(database.playerScoreDao())
    val viewModel: PlayerScoreViewModel = viewModel(factory = PlayerScoreViewModelFactory(repository))

    Box(modifier = Modifier.fillMaxSize())
    {
        Canvas(modifier = Modifier.matchParentSize())
        {
            drawIntoCanvas {
                canvas -> drawRect(color = Color.Cyan)
            }
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
        {
            Button(onClick = {
                navController.navigate("game")
                                GameObjectManager.ReloadGame()})

            {
                Text(text = "Start Game")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("Leaderboard") })
            {
                Text(text = "Scoreboard")
            }

            //Spacer(modifier = Modifier.height(16.dp))

//            Button(onClick = { navController.navigate("tutorial") })
//            {
//                Text(text = "Tutorial")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(onClick = { navController.navigate("Credits") })
//            {
//                Text(text = "Credits")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(onClick = { navController.navigate("settings") })
//            {
//                Text(text = "Settings")
//            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { exitProcess(-1) })
            {
                Text(text = "Exit")
            }
        }
    }
}
