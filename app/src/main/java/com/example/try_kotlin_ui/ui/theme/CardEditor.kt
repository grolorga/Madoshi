package com.example.try_kotlin_ui.ui.theme

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Locale


data class CardData(
    val title: String,
    val shortDescription: String,
    val longDescription: String,
    val imageUrl: String // Ссылка на изображение
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditor(card:CardItem) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    //val card  = CardItem("","","","")
    var title by remember { mutableStateOf( card?.title ?: "") } // Отображаемые данные из карточки
    var shortDescription by remember { mutableStateOf(card?.description ?: "") } // Отображаемые данные из карточки
    var longDescription by remember { mutableStateOf(card?.longdescription ?: "") } // Отображаемые данные из карточки


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
        }
    )

    // Инициализация Firestore
    val db = Firebase.firestore
    val storage = Firebase.storage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Выбор изображения
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Transparent)
                .clickable {
                    launcher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            imageUri?.let { uri ->
                Image(
                    painter = rememberImagePainter(data = uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            if (imageUri == null) {
                Text("Add Image")
            }
        }

        // Название
        TextField(
            value = title,
            onValueChange = { title = it.capitalizeWords() },
            label = { Text("Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Короткое описание
        TextField(
            value = shortDescription,
            onValueChange = { shortDescription =
                it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            },
            label = { Text("Short Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Длинное описание
        TextField(
            value = longDescription,
            onValueChange = { longDescription =
                it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            },
            label = { Text("Long Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Кнопка сохранения
        Button(
            onClick = {
                // Проверка наличия выбранного изображения
                if (imageUri != null) {
                    // Ссылка на место в Storage, где будет храниться файл
                    val storageRef = storage.reference.child("images/${imageUri?.lastPathSegment}")

                    // Загрузка изображения в Firebase Storage
                    val uploadTask = storageRef.putFile(imageUri!!)

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        // Получение ссылки на загруженное изображение
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            var imageUrl = uri.toString()

                            // Создание объекта CardData
                            val card = CardData(title, shortDescription, longDescription, imageUrl)

                            // Сохранение данных в Firestore
                            db.collection("invent")
                                .add(card)
                                .addOnSuccessListener {
                                    Toast.makeText(context,"Данные успешно сохранены", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context,"Данные не сохранены", Toast.LENGTH_LONG).show()
                                }
                        }
                    }.addOnFailureListener {
                        // Обработка ошибки загрузки изображения в Storage
                    }
                } else {
                    // Обработка случая, когда изображение не выбрано
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save")
        }
    }
}

// Функция для преобразования первой буквы к заглавной в каждом слове
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it ->
    it.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.ROOT
    ) else it.toString()
} }


