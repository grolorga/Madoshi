package com.example.try_kotlin_ui.ui.theme
// Menu.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun BottomNavigation(navController: NavController) {
    val currentScreen = remember(navController) {
        mutableStateOf("screen3") // Инициализируем текущий экран
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavigationItem(
            icon = Icons.Default.Home,
            isSelected = currentScreen.value == "screen1"
        ) {
            currentScreen.value = "screen1"
            navController.navigate("screen1")
        }

        BottomNavigationItem(
            icon = Icons.Default.Search,
            isSelected = currentScreen.value == "screen2"
        ) {
            currentScreen.value = "screen2"
            navController.navigate("screen2")
        }

        BottomNavigationItem(
            icon = Icons.Default.Person,
            isSelected = currentScreen.value == "screen3"
        ) {
            currentScreen.value = "screen3"
            navController.navigate("screen3")
        }

        BottomNavigationItem(
            icon = Icons.Default.Favorite,
            isSelected = currentScreen.value == "screen4"
        ) {
            currentScreen.value = "screen4"
            navController.navigate("screen4")
        }

        BottomNavigationItem(
            icon = Icons.Default.Settings,
            isSelected = currentScreen.value == "screen5"
        ) {
            currentScreen.value = "screen5"
            navController.navigate("screen5")
        }
    }
}

@Composable
private fun BottomNavigationItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,

    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) {
                Color.White // Изменение цвета иконки для активной кнопки
            } else {
                Color.Black
            }
        )
    }
}
