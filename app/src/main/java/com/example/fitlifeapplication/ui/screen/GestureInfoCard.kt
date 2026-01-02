package com.example.fitlifeapplication.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitlifeapplication.ui.theme.weeklyProgressCardBackground

@Composable
fun GestureInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = weeklyProgressCardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Gesture Controls Enabled", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Swipe left to delete")
            Text("• Swipe right to mark as complete")
            Text("• Long press to edit")
        }
    }
}
