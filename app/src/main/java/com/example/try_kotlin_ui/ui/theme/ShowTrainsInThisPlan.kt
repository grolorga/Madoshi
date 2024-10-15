package com.example.try_kotlin_ui.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTrainsInThisPlan(
    plan: PlanData,
    onDismiss:()->Unit,
    onDeletePlan:()->Unit
){
    var trains = plan.trainUrls
    var plansList by remember { mutableStateOf<List<Train>>(emptyList()) }
    var selectedTrain by remember {mutableStateOf<Train?>(null)}
    var editPlanName by remember { mutableStateOf(false)}
    var infoBottomSheetScaffoldState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = Modifier
            .height(700.dp)
            .background(Color.Transparent)//Вот тут красный
        ,
        sheetState = infoBottomSheetScaffoldState,
        onDismissRequest = onDismiss
    ) {
        LaunchedEffect(Unit) {
            try {
                // Получаем данные из Firestore
                val fetchedPlansList = fetchTrains(trains)
                plansList = fetchedPlansList
                // Добавим отладочную информацию
                fetchedPlansList.forEachIndexed { index, card ->
                    Log.d("TrainItem $index", "Title: ${card.trainName}")
                }
            } catch (e: Exception) {
                // Обработка ошибок при получении данных из Firestore
                Log.e("FirestoreError", "Error fetching data: ${e.message}")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row (modifier = Modifier.fillMaxWidth().padding(16.dp)){
            Text(text = plan.title,modifier = Modifier.clickable(onClick = {
                //Изменить название и сохранить в базу
                editPlanName = true
            }), style = MaterialTheme.typography.titleMedium, softWrap = true)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(imageVector = Icons.Filled.Edit, contentDescription = "edit", modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(plansList.size) { index ->
                val train = plansList[index]
                TrainUnit(
                    train = train,
                    onClick = { selectedTrain = train}
                    )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if(editPlanName){
            EditPlanDialog (
                plan = plan,
                onDismiss = {
                    editPlanName = false
                },
                onDeletePlan = {onDeletePlan()}
            )


        }
        selectedTrain?.let {train->
            TrainDetailsDialog(
            train = train,
            onDismiss = {selectedTrain = null}
            )
        }
    }




//    Column () {
//        LaunchedEffect(Unit) {
//            try {
//                // Получаем данные из Firestore
//                val fetchedPlansList = fetchTrains(trains)
//                plansList = fetchedPlansList
//                // Добавим отладочную информацию
//                fetchedPlansList.forEachIndexed { index, card ->
//                    Log.d("TrainItem $index", "Title: ${card.trainName}")
//                }
//            } catch (e: Exception) {
//                // Обработка ошибок при получении данных из Firestore
//                Log.e("FirestoreError", "Error fetching data: ${e.message}")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        LazyColumn {
//            items(plansList.size) { index ->
//                val train = plansList[index]
//                TrainUnit(
//                    train = train,
//                    onClick = { selectedTrain = train}
//                )
//            }
//        }
//        selectedTrain?.let {train->
//            TrainDetailsDialog(
//                train = train,
//                onDismiss = {selectedTrain = null}
//            )
//        }
//    }

}


// Функция для получения данных планов из Firestore
suspend fun fetchTrains(
    trainIds: List<String>
): List<Train> {
    val trainList = mutableListOf<Train>()

    try {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("trains")

        val querySnapshot = collection.get().await()

        for (document in querySnapshot.documents) {
            val trainName = document.getString("trainName") ?: "trainName"
            val inventory = document.get("inventory") as List<String>
            val exercises = document.get("exercises") as List<String>
            val tryCounts = document.get("tryCounts") as List<Int>
            val repCounts = document.get("repCounts") as List<String>
            val id = document.id

            Log.d("FetchData", "Title: $trainName")
            if(id in trainIds){
                val train = Train(
                    trainName = trainName,
                    inventory = inventory,
                    exercises = exercises,
                    tryCounts = tryCounts,
                    repCounts = repCounts
                )
                trainList.add(train)
            }else{
                continue
            }

        }
    } catch (e: Exception) {
        Log.e("FetchData", "Error fetching data: ${e.message}")
    }

    return trainList
}