package com.example.try_kotlin_ui.ui.theme

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlanDialog(
    plan: PlanData,
    onDismiss:()->Unit,
    onDeletePlan:()->Unit
){
    val context = LocalContext.current
    var title = plan.title
    var newTitle by remember { mutableStateOf(title)}
    var update by remember { mutableStateOf(false)}
    var deletePlan by remember { mutableStateOf(false)}
    var infoBottomSheetScaffoldState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = Modifier
            .height(300.dp)
            .background(Color.Transparent)//Вот тут красный
        ,
        sheetState = infoBottomSheetScaffoldState,
        onDismissRequest = onDismiss
    ) {
        TextField(
            value = newTitle,
            onValueChange = { newTitle = it},
            label = { Text("Название") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        Button(onClick = { update = true }) {
            Text("Обновить")
        }
        Button(onClick = { deletePlan = true }) {
            Text("Удалить план")
        }
    }
    if(deletePlan){
        LaunchedEffect(Unit){
            try{
                DeleteDocument(plan)
                Toast.makeText(context,"План удалён", Toast.LENGTH_SHORT).show()
                onDeletePlan()
            } catch(e: Exception) {
                // Обработка ошибок при получении данных из Firestore
                Log.e(
                    "ОШИБКА УДАЛЕНИЯ",
                    "Error : ${e.message}"
                )
            }
        }
    }
    if (update){
        LaunchedEffect(Unit) {
            try {
                UpdateDocument(newTitle, plan)
                Toast.makeText(context,"Название обновлено", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Обработка ошибок при получении данных из Firestore
                Log.e("ОШИБКА ОБНОВЛЕНИЯ", "Error: ${e.message}")
            }
        }

    }
}

suspend fun UpdateDocument(newTitle: String,plan: PlanData){
    try {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("plans")

        val querySnapshot = collection.get().await()

        for (document in querySnapshot.documents) {
            val ownerUID = document.getString("ownerUID") ?: "ownerUID"
            val title = document.getString("title") ?: "title"
            val trainUrls = document.get("trainUrls") as List<String>
            val idToUpdate = document.id

            Log.d("FetchData", "Title: $title")
            if(plan.ownerUID == ownerUID && plan.trainUrls[1]==trainUrls[1]){
                collection.document(idToUpdate).set(
                    PlanData(title = newTitle,
                    ownerUID = plan.ownerUID,
                    trainUrls = plan.trainUrls)
                ).await()
            }
        }
    } catch (e: Exception) {
        Log.e("FetchData", "Error fetching data: ${e.message}")
    }

}
suspend fun DeleteDocument(plan:PlanData){
    try {
        val firestore = FirebaseFirestore.getInstance()
        val collectionPlan = firestore.collection("plans")
        var collectionTrains = firestore.collection("trains")


        val querySnapshot = collectionPlan.get().await()

        for (document in querySnapshot.documents) {
            val ownerUID = document.getString("ownerUID") ?: "ownerUID"
            val title = document.getString("title") ?: "title"
            val trainUrls = document.get("trainUrls") as List<String>
            val idToUpdate = document.id

            Log.d("FetchData", "Title: $title")
            if(title == plan.title && trainUrls[0]==plan.trainUrls[0]){
                trainUrls.forEach{
                    collectionTrains.document(it).delete().await()
                }
                collectionPlan.document(idToUpdate).delete().await()
            }
        }
    } catch (e: Exception) {
        Log.e("ОШИБКА УДАЛЕНИЯ", "Error: ${e.message}")
    }

}
