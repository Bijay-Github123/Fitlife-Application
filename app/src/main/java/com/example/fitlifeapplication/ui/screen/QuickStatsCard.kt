package com.example.fitlifeapplication.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.fitlifeapplication.ui.theme.weeklyProgressCardBackground

@Composable
fun QuickStatsCard(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = weeklyProgressCardBackground)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = value, style = MaterialTheme.typography.titleLarge, color = tint)
                Text(text = label, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
