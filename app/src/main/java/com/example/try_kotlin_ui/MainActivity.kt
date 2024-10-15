package com.example.try_kotlin_ui


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.try_kotlin_ui.presentation.sign_in.GoogleAuthUiClient
import com.example.try_kotlin_ui.presentation.sign_in.ProfileScreen
import com.example.try_kotlin_ui.presentation.sign_in.SignInScreen
import com.example.try_kotlin_ui.presentation.sign_in.SignInViewModel
import com.example.try_kotlin_ui.ui.theme.BottomNavigation
import com.example.try_kotlin_ui.ui.theme.CardEditor
import com.example.try_kotlin_ui.ui.theme.CardItem
import com.example.try_kotlin_ui.ui.theme.ChooseInventGetExercise
import com.example.try_kotlin_ui.ui.theme.ExerciseCardEditor
import com.example.try_kotlin_ui.ui.theme.ListOfCardsExercise
import com.example.try_kotlin_ui.ui.theme.ListOfPlans
import com.example.try_kotlin_ui.ui.theme.MultipleIdenticalCards
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = {
                    BottomNavigation(navController = navController)
                }
            ) {
                Navigation(navController = navController)
            }
        }
    }
//}

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun Navigation(navController: NavController) {

        val navHostController = remember(navController) {
            navController as NavHostController
        }
        val card=CardItem ("","","","")
        val selectedCardItem by remember{ mutableStateOf<CardItem>(card) }

        NavHost(
            navController = navHostController, startDestination = "screen3", modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {

            composable("screen1") {
                val navController = rememberNavController()
                var title = ""
                var imgUrls = emptyList<String>()
                var idToUpdate = ""
                //var exerciseCardForChange:ExerciseCardData?
                NavHost(navController = navController, startDestination = "list_of_cards_exercise"){
                    composable("list_of_cards_exercise"){
                        ListOfCardsExercise("",onCardEdit ={
                            title = it.title
                            imgUrls = it.imageUrls
                            idToUpdate = it.idToUpdate.toString()
                            navController.navigate("card_editor_exercise" )
                        }
                        )

                    }
                    composable("card_editor_exercise"){
                        ExerciseCardEditor(transTitle = title, transUris = imgUrls, idToUpdate = idToUpdate)
                    }
                }
                

            }
            composable("screen2") {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "list_of_cards"){
                    composable("list_of_cards"){
                        MultipleIdenticalCards(
                            onCardEdit = {
                                //selectedCard =
                                navController.navigate("card_editor")
                            }
                        )
                    }
                    composable("card_editor"){
                        CardEditor(card = selectedCardItem)
                    }
                }
            }
            composable("screen3") {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "sign_in") {
                    composable("sign_in") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit){
                            if (googleAuthUiClient.getSignedInUser() != null ){
                                navController.navigate("profile")
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        viewModel.onSignInResult(signInResult)
                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Вы вошли успешно",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate("profile")
                                viewModel.resetState()
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }
                    composable("profile") {

                        ProfileScreen(
                            userData = googleAuthUiClient.getSignedInUser(),
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.signOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Выход выполнен",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.popBackStack()
                                }
                            },
                            onAddExercise = {
                                navController.navigate("editcard")
                            },
                            onAddExe = {
                                navController.navigate("addExe")
                            },
                            onDismissPlan = {
                                navController.navigate("profile")
                            }
                        )
                    }
                    composable("editcard"){
                        CardEditor(card = CardItem("","","",""))
                    }
                    composable("addExe"){
                        ExerciseCardEditor()
                    }
                }
                //MultipleCards()
            }
            composable("screen4") {
                //ExerciseCardEditor()
                ListOfPlans(
                    onDismissPlan = {
                        navController.navigate("screen4")
                    }
                )
                //ChatScreen()
            }
            composable("screen5") {
                //NewExerciseDialog { }
                ChooseInventGetExercise()
            }
        }
    }
}
