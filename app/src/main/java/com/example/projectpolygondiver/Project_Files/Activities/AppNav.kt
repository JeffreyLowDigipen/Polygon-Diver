package com.example.projectpolygondiver

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectpolygondiver.Project_Files.Gameplay.GameManager
import com.example.projectpolygondiver.Project_Files.Gameplay.GameState
import com.example.projectpolygondiver.Project_Files.LeaderboardData.Leaderboard_Page
import dagger.hilt.android.internal.Contexts.getApplication


@Composable
fun AppNavHost()
{
    val navController = rememberNavController()
    val context = LocalContext.current

    val gameState = GameState()

    NavHost(navController = navController, startDestination = "mainMenu")
    {
        composable("mainMenu") { MainMenu(navController) }
        composable("game") { GameManager(gameState) }
        composable("settings") { Settings(navController) }
        composable("Leaderboard") { Leaderboard_Page(navController, context = context) }
        composable("Credits") { Credits(navController) }
        composable("tutorial") { Help_Tutorial(navController) }
    }
}
