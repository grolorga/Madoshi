package com.example.try_kotlin_ui.ui.theme

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.try_kotlin_ui.presentation.sign_in.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class PlanData(
    val title: String,
    val ownerUID: String,
    var trainUrls: List<String>
)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListOfPlans(
    onDismissPlan:()->Unit
){
    var plansList by remember { mutableStateOf<List<PlanData>>(emptyList()) }
    var selectedPlan by remember { mutableStateOf<PlanData?>(null) }
    val context = LocalContext.current

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    var showCreatePlan by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        try {
            // Получаем данные из Firestore
            val fetchedPlansList = fetchPlans()
            //фильтруем планы, оставляем только конкретного пользователя
            plansList = fetchedPlansList.filter { plan->
                plan.ownerUID == googleAuthUiClient.getSignedInUser()?.userId
            }

            // Добавим отладочную информацию
            fetchedPlansList.forEachIndexed { index, card ->
                Log.d("PlanItem $index", "Title: ${card.title}")
            }
        } catch (e: Exception) {
            // Обработка ошибок при получении данных из Firestore
            Log.e("FirestoreError", "Error fetching data: ${e.message}")
        }
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Ваши тренировочные планы", style = MaterialTheme.typography.headlineSmall, softWrap = true)

    Spacer(modifier = Modifier.height(10.dp))
        if (plansList.isEmpty()){
            PlanUnit(onClick = { showCreatePlan = true }, last = true , trainName = null )
        }else{
            Column {
                LazyColumn {
                    items(plansList.size) { index ->
                        val plan = plansList[index]
                        PlanUnit(onClick = { selectedPlan = plan }, last =false , trainName = plan.title )
                    }
                }
                if (plansList.size<=2){
                    PlanUnit(onClick = { showCreatePlan = true }, last = true , trainName = null )
                }
            }


        }

        if (showCreatePlan){
            CreateNewPlan(onDismiss = {
                showCreatePlan=false
                onDismissPlan()
            })
        }
        selectedPlan?.let {
            ShowTrainsInThisPlan(
                plan = it,
                onDismiss = {
                    selectedPlan = null
                },
                onDeletePlan = {
                    onDismissPlan()
                }
            ) }
    }



}

// Функция для получения данных планов из Firestore
suspend fun fetchPlans(): List<PlanData> {
    val planList = mutableListOf<PlanData>()

    try {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("plans")

        val querySnapshot = collection.get().await()

        for (document in querySnapshot.documents) {
            val ownerUID = document.getString("ownerUID") ?: "ownerUID"
            val title = document.getString("title") ?: "title"
            val trainUrls = document.get("trainUrls") as List<String>
            val idToUpdate = document.id

            Log.d("FetchData", "Train URLS: $trainUrls")
            Log.d("FetchData", "Title: $title")

            val card = PlanData(
                ownerUID = ownerUID,
                title = title,
                trainUrls = trainUrls
            )
            planList.add(card)
        }
    } catch (e: Exception) {
        Log.e("FetchData", "Error fetching data: ${e.message}")
    }

    return planList
}