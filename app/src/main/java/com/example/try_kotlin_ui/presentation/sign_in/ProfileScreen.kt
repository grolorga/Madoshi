package com.example.try_kotlin_ui.presentation.sign_in


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.try_kotlin_ui.ui.theme.IMTDialog
import com.example.try_kotlin_ui.ui.theme.ListOfPlans

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut:()->Unit,
    onAddExercise:()->Unit,
    onAddExe:()->Unit,
    onDismissPlan:()->Unit
){
    var showIMT by remember { mutableStateOf(false) }
    var userDetails by remember { mutableStateOf<UserDetails?>(null)}
    var userDetailsChange by remember { mutableStateOf(false) }
    var scrollState = rememberScrollState()

    Column (
        modifier= Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        Arrangement.Center,
        Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.height(32.dp))
        if (userData?.profilePictureUrl != null){
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop

            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        LaunchedEffect(Unit) {
            try {
                // Получаем данные из Firestore
                val fetchedUsersList = fetchUsersDetails()
                // Поиск пользовательских данных
                fetchedUsersList.forEach {
                    if(it.uid == userData!!.userId){
                        userDetails = it
                    }
                }
            } catch (e: Exception) {
                // Обработка ошибок при получении данных из Firestore
                Log.e("FirestoreError", "Error fetching data: ${e.message}")
            }
        }
        if (userData?.username != null){
            Text(
                text = if(userDetails!=null) userDetails!!.name else userData.username,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold

            )
            Spacer(modifier = Modifier.height(16.dp))

        }
        Button(onClick = onSignOut) {
            Text(text = "Выйти")

        }
        Button(onClick = {userDetailsChange = true}
        ) {
            Text(text = "Ваши параметры")
        }
        if(userDetailsChange) UserDetailsDialog(
            userData = userData,
            userDetails = userDetails,
            onDismiss = {
                // Закрытие диалога
                userDetailsChange = false
                onDismissPlan()
            }
        )
        if(userData?.userId == "Qnv4fcNgtIayvL7vkputihFOwGo1"){
            Button(onClick = onAddExercise) {
                Text(text = "Добавить инвентарь")
            }
        }
        if(userData?.userId == "Qnv4fcNgtIayvL7vkputihFOwGo1"){
            Button(onClick = onAddExe) {
                Text(text = "Добавить упражнение")
            }
        }
        //Блоки с имт, весом, возрастом

        if(userDetails!=null){
            var boxColor=Color.Cyan
            if(userDetails!!.imt<16||userDetails!!.imt>40){
                boxColor=Color.Red
            }else if(userDetails!!.imt<18.5 && userDetails!!.imt>16||userDetails!!.imt<=40 && userDetails!!.imt>35){
                boxColor = Color(255,95,0)
            }else if(userDetails!!.imt<=35 && userDetails!!.imt>30){
                boxColor = Color(255,165,0)
            }else if(userDetails!!.imt<=30&&userDetails!!.imt>25){
                boxColor = Color.Yellow
            }
            Box(modifier = Modifier
                .padding(8.dp)
                .height(100.dp)
                .width(100.dp)
                .border(3.dp, boxColor, RoundedCornerShape(20.dp))
                .clickable ( enabled = true, onClick={showIMT = true} ),
                contentAlignment = Alignment.Center){
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ИМТ",style = MaterialTheme.typography.bodyLarge,textAlign = TextAlign.Center, modifier = Modifier
                        .padding(8.dp))
                    Text(userDetails!!.imt.toString(),style = MaterialTheme.typography.bodyLarge,textAlign = TextAlign.Center, modifier = Modifier
                        .padding(8.dp))

                }

            }

        }
        if(showIMT){
            IMTDialog(userDetails!!,onDismiss = {showIMT = false})

        }

        //Здесь нужно получать документы с существующими тренировками, если их нет передавать что last true и вызывать создание плана по клику
        //PlanUnit(onClick = { /*TODO*/ }, last = true, trainName = null)
        //PlanUnit(onClick = { /*TODO*/ }, last = false, trainName = "Тренировка 1")
        Box(Modifier.height(2000.dp)){
            //ListOfCardsExercise(showByInvent = "", onCardEdit = { /*TODO*/ })
            ListOfPlans(
                onDismissPlan = {
//                    refreshTrigger = !refreshTrigger
                    onDismissPlan()
                }
            )

        }



    }
}