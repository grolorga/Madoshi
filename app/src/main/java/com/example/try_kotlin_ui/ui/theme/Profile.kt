package com.example.try_kotlin_ui.ui.theme

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.try_kotlin_ui.R

@Composable
fun MultipleCards() {
    val cardList = List(15) {
        CardItem(
            imageResource = R.drawable.girl.toString(), // замените на ваш ресурс изображения
            title = "Заголовок",
            description = "Описание",
            longdescription = "Длинное описание"
        )
    }
    var selectedCard by remember { mutableStateOf<CardItem?>(null) }

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
