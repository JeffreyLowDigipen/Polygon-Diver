package com.example.projectpolygondiver.Project_Files.LeaderboardData

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectpolygondiver.PlayerScore
import com.example.projectpolygondiver.PlayerScoreDatabase
import com.example.projectpolygondiver.PlayerScoreRepository
import com.example.projectpolygondiver.PlayerScoreViewModel
import com.example.projectpolygondiver.PlayerScoreViewModelFactory
import com.example.projectpolygondiver.R

@Composable
fun Leaderboard_Page(navController: NavController, context: Context = LocalContext.current)
{
    val database = PlayerScoreDatabase.getDatabase(context)
    val repository = PlayerScoreRepository(database.playerScoreDao())
    val viewModel: PlayerScoreViewModel = viewModel(factory = PlayerScoreViewModelFactory(repository))
    val scores = viewModel.scores.observeAsState(initial = emptyList())

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))
    {
        Text("Leaderboard", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(2f)) {
            items(scores.value) {
                score ->
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    ScoreCanvasItem(score)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().weight(0.25f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center)
        {
            Button(onClick = { viewModel.clearScores() })
            {
                Text(text = "Clear Scores")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { navController.navigate("mainMenu") })
            {
                Text(text = "Back")
            }
        }
    }
}

@Composable
fun ScoreCanvasItem(score: PlayerScore)
{
    val textSize = 40f

    val context = LocalContext.current
    val bitmap = remember {
        val drawable = context.getDrawable(R.drawable.score)!!
        val bitmap = Bitmap.createBitmap(
            50, 50, Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, 50, 50)
        drawable.draw(canvas)
        bitmap.asImageBitmap()
    }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(4.dp))
    {
        drawRect(color = Color.Gray, size = size)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.LTGRAY
            this.textSize = textSize
        }

        val nameWidth = textPaint.measureText(score.playerName)
        val scoreWidth = textPaint.measureText(score.score.toString())

        val textY = (size.height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)

        drawContext.canvas.nativeCanvas.drawText(
            score.playerName, 80f, textY, textPaint)
        drawContext.canvas.nativeCanvas.drawText(
            score.score.toString(),
            size.width - scoreWidth - 20,
            textY,
            textPaint
        )

        drawImage(bitmap, topLeft = Offset(10f, size.height / 2 - 25))
    }
}