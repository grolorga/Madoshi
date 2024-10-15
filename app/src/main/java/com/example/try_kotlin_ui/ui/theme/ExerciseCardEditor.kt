package com.example.try_kotlin_ui.ui.theme

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class ExerciseCardData(
    val title: String,
    val shortDescription: String,
    val longDescription: String,
    val imageUrls: List<String>,
    //val inventoryTags: String,
    //Список инвентаря
    val inventoryTags: List<String>,
    val muscleGroupTags: List<String>,
    val styleOfTraining: List<String>,
    val idToUpdate: String? = null
)

@SuppressLint("UnrememberedMutableState", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCardEditor(
    transTitle: String? = null,
    //transUri: String? = null,
    transUris: List<String>? = emptyList(),
    idToUpdate: String? = null
) {
    val muscles = listOf("Грудные мышцы", "Широчайшие мышцы спины", "Трапециевидные мышцы",
        "Ромбовидные мышцы", "Дельтовидная мышца", "Бицепсы", "Трицепсы", "Квадрицепсы",
        "Икроножные мышцы", "Ягодицы", "Прямые мышцы живота", "Косые мышцы живота")
    val styles = listOf("Снижение веса", "Набор мышечной массы", "Повышение общего здоровья", "Улучшение выносливости")
    val context = LocalContext.current
    //var list: List<String>? = null
    var akkForImages = mutableListOf<String>()
    //var imageUri by remember { mutableStateOf(transUri) }
    var imageUris by remember { mutableStateOf(transUris) }
    var title by remember { mutableStateOf(transTitle) }
    var shortDescription by remember { mutableStateOf<String?>("") }
    var longDescription by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogMuscles by remember { mutableStateOf(false) }
    var showDialogStyles by remember { mutableStateOf(false) }
    // Текстовое поле, где нужно отображать выбранные упражнения
    var textFieldValue by remember { mutableStateOf("") }
    var muscleTextFieldValue by remember {mutableStateOf("")}
    var stylesTextFieldValue by remember {mutableStateOf("")}
    var selectedInventoryItems by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    var selectedMuscleTags by remember { mutableStateOf<List<String>>(emptyList())}
    var selectedStyles by remember { mutableStateOf<List<String>>(emptyList())}
    var newListForSave by remember { mutableStateOf<List<String>?>(null)}
    var inventoryList by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    // TODO: Добавить состояние для выбора тегов о группе мышц

    /*
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri.toString()
        }
    )
     */
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            imageUris = uris.map { it.toString() }
        }
    )

    // Инициализация Firestore
    val db = Firebase.firestore
    val storage = Firebase.storage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Выбор изображения
        Button(
            onClick = { launcher.launch("image/*") }
        ) {
            Text(if (imageUris?.isEmpty() == true) "Добавить изображение" else "Изменить изображения")
        }

        // Отображение выбранных изображений
        imageUris?.forEach { imageUri ->
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.Transparent)
                    .padding(8.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Название
        TextField(
            value = if(title==null) "" else title.toString(),
            onValueChange = { title = it.capitalizeWords() },
            label = { Text("Название") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Короткое описание
        TextField(
            value = shortDescription.toString(),
            onValueChange = { shortDescription =
                it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            },
            label = { Text("Короткое описание") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Длинное описание
        TextField(
            value = longDescription,
            onValueChange = { longDescription =
                it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            },
            label = { Text("Техника выполнения") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // TODO: Добавить выбор тегов о группе мышц
        TextField(
            value = muscleTextFieldValue,
            onValueChange = {selectedMuscleTags.joinToString(", ")},
            label = { Text("Рабочие группы") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                showDialogMuscles=true
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Выбрать нагрузку")
        }
        if(showDialogMuscles){
            MuscleTagsDialog(
                allTags = muscles,
                selectedTags = mutableStateOf(selectedMuscleTags),
                onDismiss = { showDialogMuscles = false },
                updateTextFieldValue = { newValue -> muscleTextFieldValue = newValue }
            )
        }
        //Выбор стиля тренировок
        TextField(
            value = stylesTextFieldValue,
            onValueChange = {selectedStyles.joinToString(", ")},
            label = { Text("Стиль тренировок") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                showDialogStyles=true
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Выбрать стиль")
        }
        if(showDialogStyles){
            MuscleTagsDialog(
                allTags = styles,
                selectedTags = mutableStateOf(selectedStyles),
                onDismiss = { showDialogStyles = false },
                updateTextFieldValue = { newValue -> stylesTextFieldValue = newValue }
            )
        }


        LaunchedEffect(Unit) {
            try {
                // Получаем данные из Firestore
                val fetchedCardList = fetchCardItems()
                inventoryList = fetchedCardList

                // Добавим отладочную информацию
                fetchedCardList.forEachIndexed { index, card ->
                    Log.d("CardItem $index", "Image Resource: ${card.imageResource}, Title: ${card.title}, Description: ${card.description}")
                }
            } catch (e: Exception) {
                // Обработка ошибок при получении данных из Firestore
                Log.e("FirestoreError", "Error fetching data: ${e.message}")
            }
        }

        // Отображение диалога с карточками инвентаря
        if (showDialog) {
            InventoryDialog(
                inventoryList = inventoryList,
                selectedItems = mutableStateOf(selectedInventoryItems),
                onDismiss = { showDialog = false },
                updateTextFieldValue = { newValue -> textFieldValue = newValue } // Передача функции для обновления текстового поля
            )
        }


        // Обновление содержимого текстового поля при изменении списка выбранных элементов
        LaunchedEffect(selectedInventoryItems) {
            textFieldValue = selectedInventoryItems.joinToString(", ") { it.title }
        }

        TextField(
            value = textFieldValue,
            onValueChange = {newListForSave = textFieldValue.split(", ")},
            label = { Text("Выбранный инвентарь") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                showDialog=true
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Выбрать инвентарь")
        }

        // Кнопка сохранения
        Button(
            onClick = {
                // Проверка наличия выбранных изображений
                if (imageUris?.isNotEmpty() == true) {
                    val storage = Firebase.storage
                    val db = Firebase.firestore
                    if(idToUpdate == null)
                    {
                        // Создание новой корутины
                        CoroutineScope(Dispatchers.IO).launch {

                            // Создание списка для отслеживания всех задач загрузки изображений
                            val uploadTasks = mutableListOf<Deferred<String>>()

// Загрузка каждого изображения в Firebase Storage
                            imageUris?.forEach { imageUri ->
                                val storageRef =
                                    storage.reference.child("exerciseImages/${imageUri.toUri().lastPathSegment}")

                                // Запуск асинхронной операции загрузки изображения и получения ссылки
                                val uploadTask = async {
                                    val uploadTask = storageRef.putFile(imageUri.toUri()).await()
                                    val downloadUrl = storageRef.downloadUrl.await().toString()
                                    downloadUrl // Возвращаем ссылку на загруженное изображение
                                }

                                uploadTasks.add(uploadTask)
                            }

// Дождитесь окончания всех задач загрузки изображений
                        val imageUrls = uploadTasks.awaitAll()
                            val excard = ExerciseCardData(
                                title.toString(),
                                shortDescription.toString(),
                                longDescription,
                                imageUrls,
                                //textFieldValue,
                                textFieldValue.split(", "),
                                muscleTextFieldValue.split(", "),
                                stylesTextFieldValue.split(", ")
                            )
                            // Сохранение данных в Firestore
                            //Toast.makeText(context, "Спис "+ (newListForSave?.get(0) ?: ""), Toast.LENGTH_SHORT).show()
                            val collection = db.collection("exercise")
                            collection.add(excard)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Данные успешно сохранены", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Ошибка сохранения данных", Toast.LENGTH_LONG).show()
                                }
                        }
                        /*
                        // Создание списка для отслеживания всех задач загрузки изображений
                        val uploadTasks = mutableListOf<UploadTask>()
                        akkForImages.clear()

                        // Загрузка каждого изображения в Firebase Storage
                        imageUris!!.forEach { imageUri ->
                            val storageRef = storage.reference.child("exerciseImages/${imageUri.toUri().lastPathSegment}")

                            // Загрузка изображения в Firebase Storage
                            val uploadTask = storageRef.putFile(imageUri.toUri())
                            uploadTasks.add(uploadTask)

                            uploadTask.addOnSuccessListener { taskSnapshot ->
                                // Получение ссылки на загруженное изображение
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    // **Добавление ссылки в список после успешной загрузки**
                                    akkForImages.add(uri.toString())
                                    Toast.makeText(context,"Изображение загружено \n"+ akkForImages[akkForImages.size-1],Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(context,"Изображение не загружено",Toast.LENGTH_LONG).show()
                            }
                        }
*/
                        // **Дождитесь окончания всех задач загрузки изображений и получения ссылок**


                    }else
                    {

                    }

                } else {
                    // Обработка случая, когда изображения не выбраны
                    Toast.makeText(context, "Выберите изображения", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Сохранить")
        }
    }
}





