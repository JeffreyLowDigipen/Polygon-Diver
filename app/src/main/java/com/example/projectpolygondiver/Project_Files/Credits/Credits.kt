package com.example.projectpolygondiver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Credits(navController: NavController)
{
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
        {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                Text("Hallo")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Hola")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ciao")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Hello")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Bonjour")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Olá")
                Spacer(modifier = Modifier.height(8.dp))
                Text("こんにちは")
                Spacer(modifier = Modifier.height(8.dp))
                Text("你好")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                Text("April")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ben")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Charlie")
                Spacer(modifier = Modifier.height(8.dp))
                Text("David")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Evelyn")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Fiona")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gareth")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Holland")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        )
        {
            Button(onClick = { navController.navigate("mainMenu") })
            {
                Text("Back")
            }
        }
    }
}
