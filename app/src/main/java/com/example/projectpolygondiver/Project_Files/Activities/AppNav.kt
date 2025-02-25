package com.example.projectpolygondiver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectpolygondiver.Project_Files.Gameplay.GameManager
import com.example.projectpolygondiver.Project_Files.Gameplay.GameState
import com.example.projectpolygondiver.Project_Files.LeaderboardData.Leaderboard_Page
import dagger.hilt.android.internal.Contexts.getApplication

class NavViewModel: ViewModel() {
    var navController: NavController? = null

}

@Composable
fun AppNavHost(navController: NavHostController, navViewModel: NavViewModel){
    val context = LocalContext.current

    LaunchedEffect (Unit) {
        navViewModel.navController = navController
    }

    NavHost(navController = navController, startDestination = "mainMenu")
    {
        composable("mainMenu") { MainMenu(navController) }
        composable("game") { GameManager(navController, context) }
        composable("settings") { Settings(navController) }
        composable("Leaderboard") { Leaderboard_Page(navController, context = context) }
        composable("Credits") { Credits(navController) }
        composable("tutorial") { Help_Tutorial(navController) }
    }
}

