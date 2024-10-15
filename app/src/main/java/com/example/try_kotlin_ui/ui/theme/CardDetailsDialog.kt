package com.example.try_kotlin_ui.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.try_kotlin_ui.R

@Composable
fun CardDetailsDialog(
    card: CardItem,
    onDismiss: () -> Unit,
    onCardDataChange: (CardItem) -> Unit, // Функция для передачи данных карточки
) {
    var showCardEditor by remember { mutableStateOf(false) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {

        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = card.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 18.dp),
                textAlign = TextAlign.Center
            )

            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(
                        data = card.imageResource // Подставьте сюда ваше поле с URL
                    ).apply(block = fun ImageRequest.Builder.() {
                        placeholder(R.drawable.girl) // Заглушка, пока изображение загружается
                    }).build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = card.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 18.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = card.longdescription,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 18.dp),
                textAlign = TextAlign.Center
            )
//            Button(onClick = {
//                // Передаем данные карточки для редактирования в CardEditor
//                onCardDataChange(card)
//                onDismiss()
//                //showCardEditor = true
//            }) {
//                Text("Редактировать карточку")
//            }
        }
        if(showCardEditor){
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "edit") {
                composable("edit") {
                    CardEditor(card = card)
                }
            }
        }

    }

}



