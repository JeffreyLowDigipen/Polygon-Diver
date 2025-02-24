package com.example.projectpolygondiver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController



@Composable
fun MainMenu(navController: NavController)
{
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
    {
        Button(onClick = { navController.navigate("game") })
        {
            Text("Start Game")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("settings") })
        {
            Text("Settings")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("Leaderboard") })
        {
            Text("Leaderboard")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("Credits") })
        {
            Text("Credits")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("help") })
        {
            Text("Help")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("exit") })
        {
            Text("Exit")
        }
    }
}