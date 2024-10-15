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
fun MultipleIdenticalCards(
    onCardEdit: (CardItem) -> Unit
) {
    var cardList by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    var selectedCard by remember { mutableStateOf<CardItem?>(null) }
    var showCardEditor by remember { mutableStateOf(false)}
    LaunchedEffect(Unit) {
        try {
            // Получаем данные из Firestore
            val fetchedCardList = fetchCardItems()
            cardList = fetchedCardList

            // Добавим отладочную информацию
            fetchedCardList.forEachIndexed { index, card ->
                Log.d("CardItem $index", "Image Resource: ${card.imageResource}, Title: ${card.title}, Description: ${card.description}")
            }
        } catch (e: Exception) {
            // Обработка ошибок при получении данных из Firestore
            Log.e("FirestoreError", "Error fetching data: ${e.message}")
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Список инвентаря", style = MaterialTheme.typography.headlineSmall, softWrap = true)
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            items(cardList.size) { index ->
                val card = cardList[index]
                CardWithImage(
                    card = card,
                    onClick = { selectedCard = card }
                )
            }
        }

    }

    selectedCard?.let { card ->
        CardDetailsDialog(
            card = card,
            onDismiss = {
                // Закрытие диалога
                selectedCard = null
            },
            onCardDataChange = {
                //showCardEditor = true
                // Обработка отредактированной карточки
                onCardEdit(selectedCard!!)
                // Например, сохранение в базе данных или другие действия
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
                CardEditor(card = selectedCard!!)
            }
        }
    }

}


data class CardItem(
    val imageResource: String,
    val title: String,
    val description: String,
    val longdescription: String
)

// Функция для получения данных из Firestore
suspend fun fetchCardItems(): List<CardItem> {
    val cardList = mutableListOf<CardItem>()

    try {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("invent")

        val querySnapshot = collection.get().await()

        for (document in querySnapshot.documents) {
            val imageResource = document.getString("imageUrl") ?: "imageUrl"
            val title = document.getString("title") ?: "title"
            val description = document.getString("shortDescription") ?: "shortDescription"
            val longdescription = document.getString("longDescription") ?: "longDescription"

            Log.d("FetchData", "Image URL: $imageResource")
            Log.d("FetchData", "Title: $title")
            Log.d("FetchData", "Description: $description")
            Log.d("FetchData", "LongDescription: $longdescription")

            val card = CardItem(
                imageResource = imageResource,
                title = title,
                description = description,
                longdescription = longdescription
            )
            cardList.add(card)
        }
    } catch (e: Exception) {
        Log.e("FetchData", "Error fetching data: ${e.message}")
    }

    return cardList
}
