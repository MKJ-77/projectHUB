package com.example.projecthub.utils

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_IS_DARK_MODE = "is_dark_mode"

    fun getTheme(context: Context): Flow<Boolean> = flow {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        emit(prefs.getBoolean(KEY_IS_DARK_MODE, false))
    }

    suspend fun saveTheme(context: Context, isDarkMode: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_DARK_MODE, isDarkMode).apply()
    }
}