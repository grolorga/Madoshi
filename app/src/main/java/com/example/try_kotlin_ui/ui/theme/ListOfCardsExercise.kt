package com.example.try_kotlin_ui.ui.theme


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun ListOfCardsExercise(
    showByInvent:String,//CardItem,
    onCardEdit: (ExerciseCardData) -> Unit
) {
    var cardList by remember { mutableStateOf<List<ExerciseCardData>>(emptyList()) }
    var selectedCard by remember { mutableStateOf<ExerciseCardData?>(null) }
    var showCardEditor by remember { mutableStateOf(false)}
    LaunchedEffect(Unit) {
        try {
            // Получаем данные из Firestore
            val fetchedCardList = fetchExerciseCardItems()
            cardList = fetchedCardList

            // Добавим отладочную информацию
            fetchedCardList.forEachIndexed { index, card ->
                Log.d("CardItem $index", "Image Resource: ${card.imageUrls}, Title: ${card.title}, Description: ${card.shortDescription}")
            }
        } catch (e: Exception) {
            // Обработка ошибок при получении данных из Firestore
            Log.e("FirestoreError", "Error fetching data: ${e.message}")
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Список упражнений", style = MaterialTheme.typography.headlineSmall, softWrap = true)
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(cardList.size) { index ->
                val card = cardList[index]
                if (showByInvent != "") {
                    if (card.inventoryTags.contains(showByInvent))
                        CardWithImageExercise(
                            card = card,
                            onClick = { selectedCard = card }
                        )
                }else
                {
                    CardWithImageExercise(
                        card = card,
                        onClick = { selectedCard = card }
                    )
                }

            }
        }
    }




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
                    onCardEdit(selectedCard!!)

                }
            )
    }
    if (showCardEditor && selectedCard != null) {
        // Сброс флага для показа редактора карточек
        //showCardEditor = false

        // Переход к экрану редактирования карточки
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "edit") {
            composable("edit") {
                ExerciseCardEditor(selectedCard!!.title, selectedCard!!.imageUrls, selectedCard!!.idToUpdate)
            }
        }
    }

}


// Функция для получения данных упражнений из Firestore
suspend fun fetchExerciseCardItems(): List<ExerciseCardData> {
    val cardList = mutableListOf<ExerciseCardData>()

    try {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("exercise")

        val querySnapshot = collection.get().await()

        for (document in querySnapshot.documents) {
            val imageUrls = document.get("imageUrls") as List<String>
            val title = document.getString("title") ?: "title"
            val description = document.getString("shortDescription") ?: "shortDescription"
            val longdescription = document.getString("longDescription") ?: "longDescription"
            val inventorytags = document.get("inventoryTags") as List<String>
            val musclegrouptags = document.get("muscleGroupTags") as List<String>
            val styletags = document.get("styleOfTraining") as List<String>
            val idToUpdate = document.id

            Log.d("FetchData", "Image URLS: $imageUrls")
            Log.d("FetchData", "Title: $title")
            Log.d("FetchData", "Description: $description")
            Log.d("FetchData", "LongDescription: $longdescription")

            val card = ExerciseCardData(
                imageUrls = imageUrls,
                title = title,
                shortDescription = description,
                longDescription = longdescription,
                inventoryTags = inventorytags,
                muscleGroupTags = musclegrouptags,
                styleOfTraining = styletags,
                idToUpdate = idToUpdate
            )
            cardList.add(card)
        }
    } catch (e: Exception) {
        Log.e("FetchData", "Error fetching data: ${e.message}")
    }

    return cardList
}