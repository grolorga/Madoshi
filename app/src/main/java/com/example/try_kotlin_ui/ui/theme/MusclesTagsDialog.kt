package com.example.try_kotlin_ui.ui.theme


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Text

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleTagsDialog(
    allTags: List<String>,
    selectedTags: MutableState<List<String>>,
    onDismiss: () -> Unit,
    updateTextFieldValue: (String) -> Unit // Функция для обновления текстового поля
) {
    val currentSelection = remember { mutableStateOf(selectedTags.value.toMutableList()) }

    Dialog(
        onDismissRequest = {
            selectedTags.value = currentSelection.value.toList()
            updateTextFieldValue(currentSelection.value.joinToString(", ") ) // Обновляем текстовое поле при закрытии диалога
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier
                .height(600.dp)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            LazyColumn {
                items(allTags) { tagItem ->
                    val isSelected = currentSelection.value.contains(tagItem)
                    Box(
                        modifier = Modifier
                            .clickable {
                                currentSelection.value = if (isSelected) {
                                    currentSelection.value.toMutableList().apply {
                                        remove(tagItem)
                                    }
                                } else {
                                    currentSelection.value.toMutableList().apply {
                                        add(tagItem)
                                    }
                                }
                            }
                            .background(if (isSelected) Color.LightGray else Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Text(tagItem)
                    }
                }
            }
        }
    }
}
