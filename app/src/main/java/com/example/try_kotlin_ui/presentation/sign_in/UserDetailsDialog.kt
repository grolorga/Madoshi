package com.example.try_kotlin_ui.presentation.sign_in



import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

public final data class UserDetails(
    val uid: String,
    val name: String,
    val age: Int,
    val gender: String,
    val height: Int,
    val weight: Int,
    val imt: Double,
    val trained: Boolean
)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsDialog(
    userData: UserData?,
    userDetails: UserDetails?,
    onDismiss: () -> Unit,
)
{
    var uid = userData!!.userId
    var name by if(userDetails!=null) remember  { mutableStateOf(userDetails?.name) } else remember  { mutableStateOf(userData?.username) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val birthYear = if (userDetails?.age != null) currentYear - userDetails.age else null
    var age by remember { mutableStateOf<Int?>(birthYear) }
    var gender by remember { mutableStateOf<String?>(userDetails?.gender) }
    var height by remember { mutableStateOf<Int?>(userDetails?.height) }
    var weight by remember { mutableStateOf<Int?>(userDetails?.weight) }
    var trained by remember { mutableStateOf<Boolean?>(userDetails?.trained)}
    val context = LocalContext.current
    // Инициализация Firestore
    val db = Firebase.firestore
    var infoBottomSheetScaffoldState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = Modifier
            .height(700.dp)
            .background(Color.Transparent)//Вот тут красный
        ,
        sheetState = infoBottomSheetScaffoldState,
        onDismissRequest = onDismiss,
        content = {
            Column (
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .height(800.dp) //Вот этот мод позволяет раскрывать второй боттом на весь первый блок
            ){


                // Имя пользователя
                TextField(
                    value = name ?: "",
                    onValueChange = {
                        name = it
                    },
                    label = { Text("Имя") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                // Год рождения

                TextField(
                    value = age?.toString() ?: "",
                    onValueChange = {
                        try {
                            age = it.toInt()
                        } catch (e: NumberFormatException) {
                            // Обработка некорректного формата числа
                            // Например, вывод сообщения об ошибке или установка значения по умолчанию
                            age = null
                        }
                    },
                    label = { Text("Год рождения") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                // Пол
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(3.dp, Color.Cyan, RoundedCornerShape(20.dp)))
                {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Пол:")
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = gender == "ж",
                                onClick = {
                                    gender = "ж"
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Женский")

                            RadioButton(
                                selected = gender == "м",
                                onClick = {
                                    gender = "м"
                                },
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            Text("Мужской")
                        }
                    }
                }

                // рост
                TextField(
                    value = height?.toString() ?: "",
                    onValueChange = {
                        height = try {
                            it.toInt()
                        } catch (e: NumberFormatException) {
                            // Обработка некорректного формата числа
                            // Например, вывод сообщения об ошибке или установка значения по умолчанию
                            null
                        }
                    },
                    label = { Text("Рост") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                // вес
                TextField(
                    value = weight?.toString() ?: "",
                    onValueChange = {
                        weight = try {
                            it.toInt()
                        } catch (e: NumberFormatException) {
                            // Обработка некорректного формата числа
                            // Например, вывод сообщения об ошибке или установка значения по умолчанию
                            null
                        }
                    },
                    label = { Text("Вес") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                // тренированность
                Box()
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Checkbox(
                            checked = if(trained!=null) trained!! else false,
                            onCheckedChange = {
                                trained = it
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Активно занимались спортом за последние 2 месяца")
                    }
                }
                Button(onClick = {
                    //Проверка года
                    if(age!! > Calendar.getInstance().get(Calendar.YEAR) || age == null || age!! < 1900){
                        Toast.makeText(context, "Введите корректный год рождения", Toast.LENGTH_LONG).show()
                    }else{
                        //Проверка роста
                        if(height == null || height!! > 250 || height!! < 40){
                            Toast.makeText(context, "Введите корректный рост", Toast.LENGTH_LONG).show()
                        }else{
                            //Проверка веса
                            if(weight == null || weight!! > 350 || weight!! < 30){
                                Toast.makeText(context, "Введите корректный вес", Toast.LENGTH_LONG).show()
                            }else{
                                //Проверка пола
                                if(gender == ""){
                                    Toast.makeText(context, "Выберите ваш пол", Toast.LENGTH_LONG).show()
                                }else{
                                    //Сохранение документа
                                    Toast.makeText(context, "Все поля заполнены корректно", Toast.LENGTH_LONG).show()
                                    if(trained==null)
                                        trained = false
                                    val imt = (weight!!.toDouble() /(height!!.toDouble()/100 * height!!.toDouble()/100))
                                    val newImt = Math.round(imt * 100.0) / 100.0
                                    val userDetails = UserDetails(
                                        uid,
                                        name!!,
                                        Calendar.getInstance().get(Calendar.YEAR) - age!!,
                                        gender.toString(),
                                        height!!,
                                        weight!!,
                                        newImt,
                                        trained!!
                                    )
                                    // Сохранение данных в Firestore
                                    val collection = db.collection("users")
                                    collection.document(uid)
                                        .set(userDetails)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Данные успешно сохранены", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Ошибка сохранения данных", Toast.LENGTH_LONG).show()
                                        }
                                }
                            }
                        }
                    }
                }) {
                    Text("Сохранить")
                }

            }


        } )

}

// Функция для получения данных пользователя из Firestore
suspend fun fetchUsersDetails(): List<UserDetails> {
    val usersList = mutableListOf<UserDetails>()

    try {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("users")

        val querySnapshot = collection.get().await()

        for (document in querySnapshot.documents) {
            val uid = document.getString("uid") ?: ""
            val name = document.getString("name") ?: ""
            val age = (document.get("age") as? Long)?.toInt() ?: 0
            val gender = document.getString("gender") ?: ""
            val height = (document.get("height") as? Long)?.toInt() ?: 0
            val weight = (document.get("weight") as? Long)?.toInt() ?: 0
            val imt = (document.get("imt") as? Double) ?: 0.0 // Избегаем ClassCastException, учитывая, что IMT может быть null или не Double
            val trained = document.getBoolean("trained") ?: false

            Log.d("FetchData", "Username: $name")
            Log.d("FetchData", "imt: $imt")

            val fetchedUser = UserDetails(
                uid = uid,
                name = name,
                age = age,
                gender = gender,
                height = height,
                weight = weight,
                imt = imt,
                trained = trained
            )
            usersList.add(fetchedUser)
        }
    } catch (e: Exception) {
        Log.e("FetchData", "Error fetching data: ${e.message}")
    }

    return usersList
}