package com.example.try_kotlin_ui.ui.theme

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryDialog(
    inventoryList: List<CardItem>,
    selectedItems: MutableState<List<CardItem>>,
    onDismiss: () -> Unit,
    updateTextFieldValue: (String) -> Unit // Функция для обновления текстового поля
) {
    val currentSelection = remember { mutableStateOf(selectedItems.value.toMutableList()) }

    Dialog(
        onDismissRequest = {
            selectedItems.value = currentSelection.value.toList()
            updateTextFieldValue(currentSelection.value.joinToString(", ") { it.title }) // Обновляем текстовое поле при закрытии диалога
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
                items(inventoryList) { inventoryItem ->
                    val isSelected = currentSelection.value.contains(inventoryItem)
                    CardWithImage(
                        card = inventoryItem,
                        onClick = {
                            currentSelection.value = if (isSelected) {
                                currentSelection.value.toMutableList().apply {
                                    remove(inventoryItem)
                                }
                            } else {
                                currentSelection.value.toMutableList().apply {
                                    add(inventoryItem)
                                }
                            }
                        },
                        isSelected = isSelected
                    )
                }
            }
        }
    }
}
