package com.example.fitlifeapplication.data.manager

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val _userId = MutableStateFlow<Long?>(getUserIdFromPrefs())
    val userId: StateFlow<Long?> = _userId.asStateFlow()

    fun saveUser(id: Long) {
        prefs.edit().putLong("user_id", id).apply()
        _userId.value = id
    }

    private fun getUserIdFromPrefs(): Long? {
        val id = prefs.getLong("user_id", -1L)
        return if (id != -1L) id else null
    }

    fun getUserId(): Long? {
        return _userId.value
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        _userId.value = null
    }
}
