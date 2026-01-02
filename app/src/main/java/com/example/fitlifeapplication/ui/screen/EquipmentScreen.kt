package com.example.fitlifeapplication.ui.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlifeapplication.data.model.Equipment
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.EquipmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentScreen(
    equipmentViewModel: EquipmentViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val equipmentUiState by equipmentViewModel.equipmentUiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Equipment List") })
        }
    ) { paddingValues ->
        EquipmentBody(
            equipmentList = equipmentUiState.equipmentList,
            onShare = {
                val equipmentListString = equipmentUiState.equipmentList.joinToString(separator = "\n") { equipment -> "- ${equipment.name}" }
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Here is the equipment list for our workout:\n$equipmentListString")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun EquipmentBody(
    equipmentList: List<Equipment>,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        Button(onClick = onShare) {
            Text("Share Equipment List")
        }
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(equipmentList) { equipment ->
                EquipmentItem(equipment = equipment)
            }
        }
    }
}

@Composable
fun EquipmentItem(equipment: Equipment) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = equipment.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}
