package com.example.try_kotlin_ui.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlanUnit(
    onClick: () -> Unit,
    last: Boolean?,
    trainName: String?
){
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(100.dp)
            .shadow(elevation = 16.dp, shape = shape, spotColor = Color.DarkGray)
            .clickable(onClick = onClick), // Обработчик клика
        shape = shape,
        //color = backgroundColor,
        //shadowElevation = shadowElevation,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            if(last == true){
                Image(imageVector = Icons.Filled.Add, contentDescription = "Добавить")
                Spacer(modifier = Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Создать новый тренировочный план", style = MaterialTheme.typography.headlineSmall, softWrap = true)
                }
            }else{
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    if (trainName!=null){
                        Text(text = trainName, style = MaterialTheme.typography.headlineSmall, softWrap = true)
                    }else{
                        Text(text = "План без названия", style = MaterialTheme.typography.headlineSmall, softWrap = true)
                    }


                }
            }


        }
    }
}