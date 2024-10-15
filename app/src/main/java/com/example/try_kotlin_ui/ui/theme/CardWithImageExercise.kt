package com.example.try_kotlin_ui.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.try_kotlin_ui.R


@Composable
fun CardWithImageExercise(
    //imageResource: String,
    //title: String,
    //description: String,
    card: ExerciseCardData,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
) {
    val backgroundColor = if (isSelected) {
        Color.Blue// Цвет тени для выбранной карточки
    } else {
        Color.Black
    }

    val shadowElevation = if (isSelected) 16.dp else 8.dp // Высота тени для выбранной карточки

    Surface(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(elevation = shadowElevation, shape = shape, spotColor = backgroundColor)
            .clickable(onClick = onClick), // Обработчик клика
        shape = shape,
        //color = backgroundColor,
        //shadowElevation = shadowElevation,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            //modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = // Заглушка, пока изображение загружается
                rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(
                    data = card.imageUrls[0] // Подставьте сюда ваше поле с URL
                ).apply(block = fun ImageRequest.Builder.() {
                    placeholder(R.drawable.kong) // Заглушка, пока изображение загружается
                }).build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(112.dp)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                var maxAllowedChars = 13
                //Text(text = card.title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text =card.title,
//                    if (card.title.length > maxAllowedChars) {
//                        "${card.title.take(maxAllowedChars)}..."
//                    } else {
//                        card.title
//                    },
                    style = MaterialTheme.typography.titleLarge,
                    softWrap = true,
                    textAlign = TextAlign.Start
                )
//                Spacer(modifier = Modifier.height(8.dp))
//                maxAllowedChars = 15
//                Text(
//                    text =
//                    if (card.shortDescription.length > maxAllowedChars) {
//                        "${card.shortDescription.take(maxAllowedChars)}..."
//                    } else {
//                        card.shortDescription
//                    },
//                    style = MaterialTheme.typography.bodyLarge,
//                    softWrap = true,
//
//                )
                //Text(text = card.description, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}