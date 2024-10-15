package com.example.try_kotlin_ui.ui.theme


import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.try_kotlin_ui.R
import com.example.try_kotlin_ui.presentation.sign_in.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsDialogExercise(
    card: ExerciseCardData,
    onDismiss: () -> Unit,
    onCardDataChange: (ExerciseCardData) -> Unit, // Функция для передачи данных карточки
) {
    var context = LocalContext.current
    var showCardEditor by remember { mutableStateOf(false) }
    var mainSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(top = 36.dp)
        ,
        sheetState = mainSheetState,
        dragHandle = null,
    ) {

            var infoBottomSheetScaffoldState = rememberBottomSheetScaffoldState()
            BottomSheetScaffold(
                sheetPeekHeight = 520.dp,
                modifier = Modifier
                    //.height(600.dp)
                    .background(Color.Red)//Вот тут красный
                ,
                scaffoldState = infoBottomSheetScaffoldState,
                sheetContent = {
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .height(1600.dp) //Вот этот мод позволяет раскрывать второй боттом на весь первый блок
                        //.verticalScroll(rememberScrollState())
                ){
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 18.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = card.shortDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 18.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = card.longDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 18.dp),
                        textAlign = TextAlign.Center
                    )
                    Box(modifier = Modifier
                        .border(3.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        .padding(8.dp)
                        , contentAlignment = Alignment.Center) {
                        LazyVerticalGrid(columns = GridCells.Adaptive(100.dp), modifier = Modifier
                            .align(Alignment.Center)) {
                            items(card.inventoryTags.size) { item ->
                                val card = card.inventoryTags[item]
                                Text(
                                    text = card,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .border(3.dp, Color.Cyan, RoundedCornerShape(20.dp))
                                        .clickable {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Упражнение задействует " + card,
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier
                        .border(3.dp, Color.LightGray, RoundedCornerShape(20.dp))
                        .padding(8.dp)
                        , contentAlignment = Alignment.Center) {
                        LazyVerticalGrid(columns = GridCells.Adaptive(100.dp), modifier = Modifier
                            .align(Alignment.Center)) {
                            items(card.muscleGroupTags.size) { item ->
                                val card = card.muscleGroupTags[item]
                                Text(
                                    text = card,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.Center)
                                        .border(3.dp, Color.Cyan, RoundedCornerShape(20.dp))
                                        .height(100.dp)
                                        .clickable {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Упражнение задействует " + card,
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        },
                                    softWrap = true,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

//                    if(googleAuthUiClient.getSignedInUser()?.userId=="Qnv4fcNgtIayvL7vkputihFOwGo1"){
//                        Button(onClick = {
//                            // Передаем данные карточки для редактирования в CardEditor
//                            onCardDataChange(card)
//                            onDismiss()
//                            //showCardEditor = true
//                        }) {
//                            Text("Редактировать карточку")
//                        }
//                    }


                    if(showCardEditor){
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "edit") {
                            composable("edit") {
                                ExerciseCardEditor()
                            }
                        }
                    }
                }

            } ) {
                // Карусель с изображениями
                val listofimg = card.imageUrls
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    initialPageOffsetFraction = 0f
                ) {
                    card.imageUrls.size
                }
                //Бокс карусели
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Yellow) //Вот тут жёлтый
                        .height(250.dp)
                        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                ) {
                    // Вертикальный Pager для изображений
                    HorizontalPager(state = pagerState) { mainIndex ->
                        // Изображение
                        //пытаемся сделать параллакс (не получилось)

                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current).data(
                                    data = listofimg[mainIndex % listofimg.size]
                                ).apply {
                                    placeholder(R.drawable.kong)
                                }.build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                //.clip(AlertDialogDefaults.shape)
                                .background(Color.Transparent),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }



    }

}