package com.example.try_kotlin_ui.ui.theme


import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseInventGetExercise() {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var longDescription by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var inventDataChanged by remember { mutableStateOf(false) }
    // Текстовое поле, где нужно отображать выбранные упражнения
    var textFieldValue by remember { mutableStateOf("") }
    var selectedInventoryItems by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    var inventoryList by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    // TODO: Добавить состояние для выбора тегов о группе мышц

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
            onValueChange = {},
            label = { Text("Выбранный инвентарь") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                showDialog=true
                inventDataChanged=true
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Выбрать инвентарь")
        }

        if (inventDataChanged){
            Box(modifier = Modifier
                .fillMaxSize()){
                var chosen = ""//CardItem("","","","")
                try {
                    chosen = textFieldValue
                    Log.e("Chosen inventory",chosen)

                }catch (e:Exception){
                    Log.e("Empty list","list of chosen is empty")
                }
                ListOfCardsExercise(chosen, onCardEdit = {})
            }
        }

    }
}
