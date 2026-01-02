package com.example.fitlifeapplication.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitlifeapplication.ui.components.FitLifeTopAppBar
import com.example.fitlifeapplication.viewmodel.AppViewModelProvider
import com.example.fitlifeapplication.viewmodel.DelegateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegateScreen(
    navController: NavController,
    delegateViewModel: DelegateViewModel = viewModel(factory = AppViewModelProvider.Factory),
    routineId: Long? = null
) {
    val uiState by delegateViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(routineId) {
        routineId?.let {
            delegateViewModel.loadRoutine(it)
        }
    }

    val contactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let {
            // Querying contacts provider
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            
            // We need to resolve the contact URI to get the phone number.
            // Note: PickContact returns a contact URI, which might point to a contact that has multiple phone numbers
            // or we might need to query CommonDataKinds.Phone directly depending on the URI returned.
            // The URI returned by PickContact is usually a content://com.android.contacts/contacts/lookup/... 
            // We should query the Phone.CONTENT_URI joined with the contact ID or look it up differently.
            
            // However, a simpler and more robust way often involves querying the URI provided directly 
            // if it points to a specific data row, or querying the Phone table with the contact ID.
            
            // Let's try to robustly query for a phone number associated with this contact.
            try {
                // First, get the contact ID from the URI
                val cursor = context.contentResolver.query(it, null, null, null, null)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val idIndex = c.getColumnIndex(ContactsContract.Contacts._ID)
                        val hasPhoneIndex = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                        
                        val id = if (idIndex != -1) c.getString(idIndex) else null
                        val hasPhone = if (hasPhoneIndex != -1) c.getString(hasPhoneIndex) else "0"
                        
                        if (id != null && hasPhone == "1") {
                            val phones = context.contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                                arrayOf(id),
                                null
                            )
                            phones?.use { p ->
                                if (p.moveToFirst()) {
                                    val numberIndex = p.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val nameIndex = p.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                    
                                    val number = if (numberIndex != -1) p.getString(numberIndex) else ""
                                    val name = if (nameIndex != -1) p.getString(nameIndex) else ""
                                    
                                    delegateViewModel.updateFriendName(name)
                                    delegateViewModel.updateMobileNumber(number)
                                }
                            }
                        } else {
                           Toast.makeText(context, "Selected contact has no phone number", Toast.LENGTH_SHORT).show() 
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback or error handling
                e.printStackTrace()
                Toast.makeText(context, "Error retrieving contact details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            contactLauncher.launch(null)
        } else {
            Toast.makeText(context, "Permission denied to read contacts", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            FitLifeTopAppBar(
                navController = navController,
                showGreeting = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE0B2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Share, contentDescription = "Delegate Icon", tint = Color(0xFFFB8C00), modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Delegate Task", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Send equipment reminder to a friend", color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.friendName,
                onValueChange = { delegateViewModel.updateFriendName(it) },
                label = { Text("Friend's Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.mobileNumber,
                onValueChange = { delegateViewModel.updateMobileNumber(it) },
                label = { Text("Mobile Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            contactLauncher.launch(null)
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    }) {
                        Icon(Icons.Default.ContactPhone, contentDescription = "Pick Contact", tint = Color(0xFF2E7D32))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simplified Dropdown for workout selection
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = uiState.selectedRoutine?.title ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Workout") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    uiState.routines.forEach { routine ->
                        DropdownMenuItem(
                            text = { Text(routine.title) },
                            onClick = {
                                delegateViewModel.selectRoutine(routine)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.messagePreview,
                onValueChange = { /* In a real app, you might allow editing */ },
                label = { Text("Message Preview") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    delegateViewModel.sendDelegation()
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:${uiState.mobileNumber}")
                        putExtra("sms_body", uiState.messagePreview)
                    }
                    context.startActivity(intent)
                },
                enabled = uiState.isSendEnabled,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Reminder", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Light blue/pale background
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFF1976D2))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "How it works",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Select a workout routine.\n2. Choose a friend to delegate to.\n3. Send an SMS with the equipment list.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF0D47A1)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
