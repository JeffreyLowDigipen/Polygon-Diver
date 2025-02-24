package com.example.projectpolygondiver

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost()
{
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "mainMenu")
    {
        composable("mainMenu") { MainMenu(navController) }
        composable("game") { Game(navController) }
        composable("settings") { Settings(navController) }
        composable("Leaderboard") { Leaderboard_Page(navController) }
        composable("Credits") { Credits(navController) }
        composable("tutorial") { Help_Tutorial(navController) }
    }
}