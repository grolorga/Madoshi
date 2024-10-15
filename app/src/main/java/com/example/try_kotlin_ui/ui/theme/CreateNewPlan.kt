package com.example.try_kotlin_ui.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.try_kotlin_ui.presentation.sign_in.GoogleAuthUiClient
import com.example.try_kotlin_ui.presentation.sign_in.UserDetails
import com.example.try_kotlin_ui.presentation.sign_in.fetchUsersDetails
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

data class Train(
    val trainName: String,
    val inventory: List<String>,
    val exercises: List<String>,
    /*
    TODO Переводим подходы в список int, а повторения в список списков int и создаём функцию их генерации
     */
    var tryCounts: List<Int>,
    var repCounts: List<String>
)

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewPlan(
    onDismiss:() -> Unit
){
    var showDialog by remember { mutableStateOf(false) }
    var inventoryList by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    var selectedInventoryItems by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    var exerciseList by remember {mutableStateOf<List<ExerciseCardData>>(emptyList())}
    var textFieldValue by remember { mutableStateOf("") }
    val timeTags = listOf("30 минут", "1 час", "1,5 часа", "2 часа", "2,5 часа", "3 часа")
    var selectedTime by remember { mutableStateOf<String?>(null) } // Состояние для отслеживания выбранного времени
    val targetTags = listOf("Снижение веса", "Набор мышечной массы", "Повышение общего здоровья", "Улучшение выносливости")
    var selectedTarget by remember { mutableStateOf<String?>(null) } // Состояние для отслеживания выбранной цели
    val numberOfTrainings = listOf(3,4,6)
    var selectedTrainingCount by remember { mutableStateOf<Int?>(null) } // Состояние для отслеживания выбранного количества тренировок
    val context = LocalContext.current
    // Инициализация Firestore
    val db = Firebase.firestore

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

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
                .verticalScroll(rememberScrollState())
                .height(800.dp) //Вот этот мод позволяет раскрывать второй боттом на весь первый блок
        ) {

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Создание плана",
                    style = MaterialTheme.typography.headlineLarge,
                    softWrap = true
                )
                LaunchedEffect(Unit) {
                    try {
                        // Получаем данные из Firestore
                        val fetchedCardList = fetchCardItems()
                        inventoryList = fetchedCardList

                        // Добавим отладочную информацию
                        fetchedCardList.forEachIndexed { index, card ->
                            Log.d(
                                "CardItem $index",
                                "Image Resource: ${card.imageResource}, Title: ${card.title}, Description: ${card.description}"
                            )
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
                        updateTextFieldValue = { newValue ->
                            textFieldValue = newValue
                        } // Передача функции для обновления текстового поля
                    )
                }


                // Обновление содержимого текстового поля при изменении списка выбранных элементов
                LaunchedEffect(selectedInventoryItems) {
                    textFieldValue = selectedInventoryItems.joinToString(", ") { it.title }
                }

                TextField(
                    value = textFieldValue,
                    onValueChange = {},
                    label = { Text("Выберанный инвентарь") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Button(
                    onClick = {
                        showDialog = true
                    },
                    //modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Выберите инвентарь, который хотите использовать")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .border(3.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Выберите время, которе можете уделять тренировке",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)) {
                            items(timeTags.size) { index ->
                                val time = timeTags[index]
                                val isSelected = time == selectedTime
                                val color = if (isSelected) Color.Green else Color.Cyan
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .border(3.dp, color, RoundedCornerShape(20.dp))
                                        .clickable {
                                            selectedTime =
                                                time // Устанавливаем выбранное время при щелчке
                                        },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .border(3.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Выберите вашу цель",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                            items(targetTags.size) { index ->
                                val target = targetTags[index]
                                val isSelected = target == selectedTarget
                                val color = if (isSelected) Color.Green else Color.Cyan
                                Text(
                                    text = target,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .height(50.dp)
                                        .border(3.dp, color, RoundedCornerShape(20.dp))
                                        .clickable {
                                            selectedTarget =
                                                target // Устанавливаем выбранное время при щелчке
                                        },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .border(3.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        .padding(8.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Выберите количество тренировок в неделю",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)) {
                            items(numberOfTrainings.size) { index ->
                                val count = numberOfTrainings[index]
                                val isSelected = count == selectedTrainingCount
                                val color = if (isSelected) Color.Green else Color.Cyan
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .border(3.dp, color, RoundedCornerShape(20.dp))
                                        .clickable {
                                            selectedTrainingCount =
                                                count // Устанавливаем выбранное количество тренировок при щелчке
                                        },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {
                try {
                    // Получаем данные из Firestore
                    val fetchedCardList = fetchExerciseCardItems()
                    exerciseList = fetchedCardList

                    // Добавим отладочную информацию
                    fetchedCardList.forEachIndexed { index, card ->
                        Log.d("CardItem $index", "Image Resource: ${card.imageUrls}, Title: ${card.title}, Description: ${card.shortDescription}")
                    }
                } catch (e: Exception) {
                    // Обработка ошибок при получении данных из Firestore
                    Log.e("FirestoreError", "Error fetching data: ${e.message}")
                }
            }
            //Получаем данные пользователя из базы
            var user by remember { mutableStateOf<UserDetails?>(null)}

            LaunchedEffect(Unit){
                try{
                    // Получаем данные из Firestore
                    val fetchedUsersList = fetchUsersDetails()
                    // Поиск пользовательских данных
                    fetchedUsersList.forEach {
                        if(it.uid == googleAuthUiClient.getSignedInUser()?.userId){
                            user = it
                        }
                    }
                }catch(e: Exception){
                    // Обработка ошибок при получении данных из Firestore
                    Log.e("FirestoreError", "Error fetching data: ${e.message}")
                }

            }
            var exeCount = 0
            var podhCount = emptyList<Int>().toMutableList()
            var repCount = emptyList<List<Int>>().toMutableList()
            Button(onClick = {
                var newPlan = PlanData(
                    title= "План от "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")).toString(),
                    ownerUID = googleAuthUiClient.getSignedInUser()?.userId.toString(),
                    trainUrls = emptyList()
                )
                when(selectedTime){
                    "30 минут"->{exeCount = 2}
                    "1 час"->{exeCount = 3}
                    "1,5 часа"->{exeCount = 4}
                    "2 часа"->{exeCount = 5}
                    "2,5 часа"->{exeCount = 5}
                    "3 часа"->{exeCount = 6}
                }
                fun generateReps(countOf:List<Int>, reps:List<List<Int>>, min:Int, max:Int): MutableList<List<Int>> {
                    var generated = reps.toMutableList()

                    // Для каждого числа в списке countOf добавляем случайные значения в generated
                    for (count in countOf) {
                        val randomReps = mutableListOf<Int>()
                        randomReps.add(Random.nextInt(5, 10))
                        for (i in 1 until count) {
                            randomReps.add(Random.nextInt(min, max))
                        }
                        generated.add(randomReps)
                    }
                    return generated
                }
                var min = 0
                var max = 0
                when(selectedTarget){
                    "Снижение веса"->{
                        for(ind in 0..exeCount){
                            podhCount.add(Random.nextInt(3,4))
                            //podhCount+=Random.nextInt(3,4)
                        }
                        //podhCount = 4
                        min = 8
                        max = 20
                        //repCount = generateReps(podhCount,repCount, min, max)
//                            listOf(Random.nextInt(5,10), Random.nextInt(8,12),
//                            Random.nextInt(10,19), Random.nextInt(10,19))
                    }
                    "Набор мышечной массы"->{
                        for(ind in 0..exeCount){
                            podhCount.add(Random.nextInt(3,4))
                            //podhCount+=Random.nextInt(3,4)
                        }
                        //podhCount = 4
                        min = 6
                        max = 16
                        //repCount = generateReps(podhCount, repCount, 6, 16)
//                            listOf(Random.nextInt(5,9), Random.nextInt(6,9),
//                            Random.nextInt(7,10), Random.nextInt(8,15))
                    }
                    "Повышение общего здоровья"->{
                        for(ind in 0..exeCount){
                            podhCount.add(Random.nextInt(2,3))
                        }
                        //podhCount = 3
                        min = 5
                        max = 12
                        //repCount = generateReps(podhCount, repCount, 5, 12)
//                            listOf(Random.nextInt(5,10),Random.nextInt(5,10),
//                            Random.nextInt(5,12))
                    }
                    "Улучшение выносливости"->{
                        for(ind in 1..exeCount){
                            podhCount.add(Random.nextInt(4,5))
                        }
                        //podhCount = 5
                        min = 8
                        max = 20
                        //repCount = generateReps(podhCount, repCount, 10, 20)
//                            listOf(Random.nextInt(5,10), Random.nextInt(10,14),
//                            Random.nextInt(10,16), Random.nextInt(12,20),
//                            Random.nextInt(10,20))
                    }
                }
                if(user!=null){
                    if(user!!.age>40||user!!.age<15){
                        exeCount--
                        podhCount = podhCount.dropLast(1).toMutableList()
                    }
                    if(user!!.gender == "ж"){
//                        podhCount.forEach{
//                            podhCount[it] -= 1
//                        }
                        for(i in 0..podhCount.size-1){
                            podhCount[i] -= 1
                        }
                        Log.e(
                            "PodhCount[0]",
                            ""+podhCount[0]
                        )
                    }
                    if(user!!.imt>40.0 || user!!.imt<16.0){
                        //podhCount = 2
                        for(i in 0..podhCount.size-1){
                            podhCount[i] = 2
                        }
                    }else if(user!!.imt >16.0 && user!!.imt<18.5){
                        for(i in 0..podhCount.size-1){
                            podhCount[i] = podhCount[i]--
                        }
                        //podhCount--

                    }else if(user!!.imt >35.0 && user!!.imt<40.0){
                        for(i in 0..podhCount.size-1){
                            podhCount[i] = podhCount[i]--
                        }
                        //podhCount--
                    }
                    if(user!!.trained){
                        for(i in 0..podhCount.size-1){
                            podhCount[i] = podhCount[i]--
                        }
                        min+=2
                        max+=2
                    }
                    Log.e(
                        "PodhCount[0]",
                        ""+podhCount[0]
                    )
                }
                if (podhCount[0]==0){
                    for(ind in 0..exeCount-1){
                        podhCount[ind]=Random.nextInt(1,2)
                    }
                }


                val selfWeightExe = exerciseList.filter{
                    it.inventoryTags.size == 1 && it.inventoryTags.contains("Собственный вес")
                }
                val filteredExerciseCards = exerciseList.filter { exerciseCard ->
                    exerciseCard.inventoryTags.all { tag -> textFieldValue.split(", ").contains(tag) }
                }
                var exercises = filteredExerciseCards

                val chestTrip = exercises.filter{ exe ->
                    listOf("Грудные мышцы","Трицепсы").all{tag-> tag in exe.muscleGroupTags}
                }
                val backBic = exercises.filter{ exe ->
                    listOf("Широчайшие мышцы спины","Бицепсы").all{tag-> tag in exe.muscleGroupTags}
                }
                val legs = exercises.filter{ exe ->
                    listOf("Квадрицепсы", "Икроножные мышцы", "Ягодицы").all{tag-> tag in exe.muscleGroupTags}
                }
                val kor = exercises.filter{exe->
                    listOf("Прямые мышцы живота", "Косые мышцы живота").all{tag-> tag in exe.muscleGroupTags}
                }
                val chest = exercises.filter{ exe ->
                listOf("Грудные мышцы").all{tag-> tag in exe.muscleGroupTags}
                }
                val spine = exercises.filter{ exe ->
                listOf("Широчайшие мышцы спины").all{tag-> tag in exe.muscleGroupTags}
                }
                val tricep = exercises.filter{ exe ->
                listOf("Трицепсы").all{tag-> tag in exe.muscleGroupTags}
                }
                val bicep = exercises.filter { exe->
                    listOf("Бицепсы").all{tag-> tag in exe.muscleGroupTags}
                }
                val shold = exercises.filter{ exe ->
                    listOf("Дельтовидная мышца").all{tag-> tag in exe.muscleGroupTags}
                }

                var chestTripExercises = chestTrip.shuffled().take(exeCount)
                var backBicExercises = backBic.shuffled().take(exeCount)
                var legsExercises = legs.shuffled().take(exeCount)
                var korExercises = kor.shuffled().take(exeCount)
                var chestExercises = chest.shuffled().take(exeCount)
                var spineExercises = spine.shuffled().take(exeCount)
                var tricepExercises = tricep.shuffled().take(exeCount)
                var bicepExercises = bicep.shuffled().take(exeCount)
                var sholdExercises = shold.shuffled().take(exeCount)
                var bicandtripExercises = emptyList<ExerciseCardData>()
                if(exeCount%2==0){
                    bicandtripExercises = tricepExercises.take(exeCount/2) + bicepExercises.take(exeCount/2)
                }else{
                    bicandtripExercises = tricepExercises.take(exeCount/2+1) + bicepExercises.take(exeCount/2-1)
                }
                //Добавим проверки на наличие вообще упражнений, чтобы не было пустых тренировок,
                // также чтобы они были укомплектованы в полном объёме
                fun Dobor(picked:List<ExerciseCardData>): List<ExerciseCardData> {
                    val ret = picked.toMutableList()
                    if(picked.size<exeCount){
                        val countOfUnpicked = exeCount-picked.size

                        for (index in 0..countOfUnpicked){
                            //ret+=exercises.take(1)
//                            ret+=selfWeightExe.filter { exe->
//                                listOf(selectedTarget).all{tag-> tag in exe.styleOfTraining}
//                            }.shuffled().take(1)
                            ret+=exercises.filter{ exe ->
                                //selectedInventoryItems.all{tag-> tag in exe.inventoryTags}
                                listOf(selectedTarget).all{tag-> tag in exe.styleOfTraining}
                            }.shuffled().take(1)
                        }
                    }
                    return ret
                }

                //проверяем каждый набор на наличие и добираем упражнения
                chestTripExercises = Dobor(chestTripExercises)
                backBicExercises = Dobor(backBicExercises)
                legsExercises = Dobor(legsExercises)
                korExercises = Dobor(korExercises)
                chestExercises = Dobor(chestExercises)
                spineExercises = Dobor(spineExercises)
                sholdExercises = Dobor(sholdExercises)
                bicandtripExercises = Dobor(bicandtripExercises)


                val repCountData = repCount.map { sublist ->
                    sublist.toString()  // Преобразовываем числа в строки
                }

                //1 грудь трицепс
                val tfch = Train(
                    trainName = "Тренировка грудь трицепс",
                    inventory = textFieldValue.split(", "),
                    exercises = chestTripExercises.map { exe->
                        exe.title
                    },
                    tryCounts = podhCount,
                    repCounts = generateReps(podhCount, repCount,min,max).map{sub->
                        sub.toString()
                    }
                )
                //2 спина бицепс
                val tfsb = Train(
                    trainName = "Тренировка спина бицепс",
                    inventory = textFieldValue.split(", "),
                    exercises = backBicExercises.map { exe->
                        exe.title
                    },
                    tryCounts = podhCount,
                    repCounts = generateReps(podhCount, repCount,min,max).map{sub->
                        sub.toString()
                    }
                )
                //3 ноги
                val tfl = Train(
                    trainName = "Тренировка ноги",
                    inventory = textFieldValue.split(", "),
                    exercises = legsExercises.map { exe->
                        exe.title
                    },
                    tryCounts = podhCount,
                    repCounts = generateReps(podhCount, repCount,min,max).map{sub->
                        sub.toString()
                    }
                )
                //4 кор
                val tfko = Train(
                    trainName = "Тренировка кор",
                    inventory = textFieldValue.split(", "),
                    exercises = korExercises.map { exe->
                        exe.title
                    },
                    tryCounts = podhCount,
                    repCounts = generateReps(podhCount, repCount,min,max).map{sub->
                        sub.toString()
                    }
                )
                //1 грудь -
                val tfchest = Train(
                    trainName = "Тренировка грудь",
                    inventory = textFieldValue.split(", "),
                    exercises = chestExercises.map { exe->
                        exe.title
                    },
                    tryCounts = podhCount,
                    repCounts = generateReps(podhCount, repCount,min,max).map{sub->
                        sub.toString()
                    }
                )
                //2 ноги+
                //3 плечи -
                val tfsholders = Train(
                    trainName = "Тренировка плечи",
                    inventory = textFieldValue.split(", "),
                    exercises = sholdExercises.map { exe->
                        exe.title
                    },
                    tryCounts = podhCount,
                    repCounts = generateReps(podhCount, repCount,min,max).map{sub->
                        sub.toString()
                    }
                )
                //4 спина -
                val tfspine = Train(
                    trainName = "Тренировка спина",
                    inventory = textFieldValue.split(", "),
                    exercises = spineExercises.map { exe->
                        exe.title
                    },
                    tryCounts = podhCount,
                    repCounts = generateReps(podhCount, repCount,min,max).map{sub->
                        sub.toString()
                    }
                )
                //5 трицепс и бицепс-
                val tfbictrip = Train(
                    trainName = "Тренировка руки",
                    inventory = textFieldValue.split(", "),
                    exercises = bicandtripExercises.map { exe->
                        exe.title
                    },
                    tryCounts = podhCount,
                    repCounts = generateReps(podhCount, repCount,min,max).map{sub->
                        sub.toString()
                    }
                )
                //6 кор +

                when(selectedTrainingCount) {
                    3->{


                        CoroutineScope(Dispatchers.IO).launch {
                            val collection = db.collection("trains")
                            val trainUrls = mutableListOf<String>()

                            // Добавляем тренировки и сохраняем их ID
                            val tfchTask = collection.add(tfch).await()
                            val tfsbTask = collection.add(tfsb).await()
                            val tflTask = collection.add(tfl).await()

                            val tasks = listOf(tfchTask, tfsbTask, tflTask)

                            tasks.forEach { documentReference ->
                                documentReference?.let {
                                    trainUrls.add(it.id)
                                }
                            }

                            // Добавляем trainUrls в newPlan
                            newPlan.trainUrls = trainUrls
                            val collectionForPlan = db.collection("plans")
                            collectionForPlan.add(newPlan)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "План сохранён", Toast.LENGTH_LONG).show()
                                    onDismiss()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Ошибка сохранения плана", Toast.LENGTH_LONG).show()
                                }

                        }

                    }
                    4->{
                        //1 грудь трицепс
                        //2 спина бицепс
                        //3 ноги
                        //4 кор

                        CoroutineScope(Dispatchers.IO).launch {
                            val collection = db.collection("trains")
                            val trainUrls = mutableListOf<String>()

                            // Добавляем тренировки и сохраняем их ID
                            val tfchTask = collection.add(tfch).await()
                            val tfsbTask = collection.add(tfsb).await()
                            val tflTask = collection.add(tfl).await()
                            val tfkoTask = collection.add(tfko).await()

                            val tasks = listOf(tfchTask, tfsbTask, tflTask, tfkoTask)

                            tasks.forEach { documentReference ->
                                documentReference?.let {
                                    trainUrls.add(it.id)
                                }
                            }

                            // Добавляем trainUrls в newPlan
                            newPlan.trainUrls = trainUrls
                            val collectionForPlan = db.collection("plans")
                            collectionForPlan.add(newPlan)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "План сохранён", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Ошибка сохранения плана", Toast.LENGTH_LONG).show()
                                }
                            onDismiss
                        }

                    }
                    6->{
                        //1 грудь -
                        //2 ноги+
                        //3 плечи -
                        //4 спина -
                        //5 трицепс и бицепс-
                        //6 кор +

                        CoroutineScope(Dispatchers.IO).launch {
                            val collection = db.collection("trains")
                            val trainUrls = mutableListOf<String>()

                            // Добавляем тренировки и сохраняем их ID
                            val tflTask = collection.add(tfl).await()
                            val tfkoTask = collection.add(tfko).await()
                            val tfshTask = collection.add(tfsholders).await()
                            val tfchTask = collection.add(tfchest).await()
                            val tfspTask = collection.add(tfspine).await()
                            val tfhaTask = collection.add(tfbictrip).await()

                            val tasks = listOf(tflTask, tfkoTask, tfshTask, tfchTask, tfspTask, tfhaTask)

                            tasks.forEach { documentReference ->
                                documentReference?.let {
                                    trainUrls.add(it.id)
                                }
                            }

                            // Добавляем trainUrls в newPlan
                            newPlan.trainUrls = trainUrls
                            val collectionForPlan = db.collection("plans")
                            collectionForPlan.add(newPlan)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "План сохранён", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Ошибка сохранения плана", Toast.LENGTH_LONG).show()
                                }
                            onDismiss
                        }
                    }
                }
            }) {
                Text(text = "Генерация плана")
            }

        }


    }
}