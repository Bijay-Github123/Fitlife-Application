package com.example.fitlifeapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlifeapplication.data.model.Equipment
import com.example.fitlifeapplication.data.repository.EquipmentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EquipmentViewModel(private val equipmentRepository: EquipmentRepository) : ViewModel() {

    val equipmentUiState: StateFlow<EquipmentUiState> =
        equipmentRepository.getAllEquipment().map { EquipmentUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = EquipmentUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class EquipmentUiState(val equipmentList: List<Equipment> = listOf())
