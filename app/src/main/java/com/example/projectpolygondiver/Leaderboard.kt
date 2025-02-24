package com.example.projectpolygondiver

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.room.Entity

@Entity(tableName = "leaderboard")
data class Leaderboard(val playerName: String, val score: Int)

fun saveScore(context: Context, playerName: String, score: Int)
{
    val sharedPreferences = context.getSharedPreferences("Leaderboard", Context.MODE_PRIVATE)
}

@Composable
fun Leaderboard_Page(nvaController: NavController)
{

}