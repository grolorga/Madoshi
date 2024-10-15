package com.example.try_kotlin_ui.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainDetailsDialog(
    train: Train,
    onDismiss: ()->Unit
){
    var selectedCard by remember { mutableStateOf<ExerciseCardData?>(null) }
    var exes by remember { mutableStateOf<List<ExerciseCardData>>(emptyList()) }
    var infoBottomSheetScaffoldState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = Modifier
            .height(700.dp)
            .background(Color.Transparent)//Вот тут красный
        ,
        sheetState = infoBottomSheetScaffoldState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = train.trainName,
                    style = MaterialTheme.typography.headlineSmall,
                    softWrap = true
                )
            }
            LaunchedEffect(Unit) {
                try {
                    var fetchedExes = fetchExerciseCardItems()
                    exes = fetchedExes.filter {
                        it.title in train.exercises
                    }
                } catch (e: Exception) {
                    Log.e(
                        "Error fetching",
                        "Error fetching exercises in TrainDetailsDialog: ${e.message}"
                    )
                }
            }
            Log.e(
                "СКОЛЬКО УПРАЖНЕНИЙ",
                ""+exes.size.toString()
            )
            LazyColumn(content = {
                items(exes.size) { index ->
                    //train.tryCounts = train.tryCounts.chunked(exes.size-1).flatten()
                    //train.repCounts = train.repCounts.chunked(exes.size-1).flatten()
                    var card = exes[index]
                    if (index<6){
                        CardWithImageExercise(card = card, onClick = { selectedCard = card })
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Количесво подходов: " + train.tryCounts.getOrNull(index),
                            style = MaterialTheme.typography.bodyLarge,
                            softWrap = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Повторения: " + train.repCounts.getOrNull(index),
                            style = MaterialTheme.typography.headlineSmall,
                            softWrap = true
                        )
                    }

                }
            })
            selectedCard?.let { card ->
                CardDetailsDialogExercise(
                    card = card,
                    onDismiss = {
                        // Закрытие диалога
                        selectedCard = null
                    },
                    onCardDataChange = {
                        //showCardEditor = true
                        // Обработка отредактированной карточки
                        //onCardEdit(selectedCard!!)

                    }
                )
            }
        }
    }
}