package com.example.try_kotlin_ui.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.try_kotlin_ui.presentation.sign_in.UserDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IMTDialog(
    user:UserDetails,
    onDismiss:()->Unit
){
    var infoBottomSheetScaffoldState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        modifier = Modifier
            .height(500.dp)
            .background(Color.Transparent),
        sheetState = infoBottomSheetScaffoldState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(1000.dp)
                .verticalScroll(rememberScrollState())
            ,
            horizontalAlignment = Alignment.CenterHorizontally
            ){
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .align(CenterHorizontally),
                text = "Индекс массы тела",
                softWrap = true,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .align(CenterHorizontally),
                text = "это числовое значение, используемое для оценки соотношения между массой " +
                        "и ростом человека. ИМТ широко используется для оценки избыточного или недостаточного" +
                        " веса и может указывать на риски связанные с здоровьем, такие как ожирение" +
                        " или недостаточный вес. Однако следует помнить, что ИМТ не является абсолютным" +
                        " показателем здоровья, и другие факторы, такие как распределение жировой массы" +
                        " и общее физическое состояние, также могут играть важную роль.",
                softWrap = true,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            var supText = "Нормальная масса тела"
            if(user.imt<16){
                supText = "Значительный дефицит массы тела"
            }else if(user.imt<18.5 && user.imt>16){
                supText = "Дефицит массы тела"
            }else if(user.imt<=35 && user.imt>30){
                supText = "Ожирение первой степени"
            }else if(user.imt<=30&&user.imt>25){
                supText = "Лишний вес"
            }else if(user.imt<=40 && user.imt>35){
                supText = "Ожирение второй степени"
            }else if(user.imt>40){
                supText = "Ожирение третьей степени"
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .align(CenterHorizontally),
                text = "Ваш ИМТ "+user.imt+" значит, что у вас "+supText,
                softWrap = true,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

    }
}