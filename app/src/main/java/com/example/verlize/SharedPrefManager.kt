package com.example.verlize

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class SharedPrefManager private constructor(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "ParasiteKeyboardPrefs"
        private const val PARASITE_WORDS = "parasite_words"
        private const val REPLACEMENT_STATS = "replacement_stats"
        private const val VIBRATION_ENABLED = "vibration_enabled"
        private const val SOUND_ENABLED = "sound_enabled"
        private const val AUTO_REPLACE = "auto_replace"
        
        @Volatile
        private var INSTANCE: SharedPrefManager? = null
        
        fun getInstance(context: Context): SharedPrefManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPrefManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun getParasiteWords(): Set<String> {
        val json = prefs.getString(PARASITE_WORDS, "[]")
        val type = object : TypeToken<Set<String>>() {}.type
        return gson.fromJson(json, type) ?: emptySet()
    }
    
    fun saveParasiteWords(words: Set<String>) {
        val json = gson.toJson(words)
        prefs.edit().putString(PARASITE_WORDS, json).apply()
    }
    
    fun addParasiteWord(word: String) {
        val words = getParasiteWords().toMutableSet()
        words.add(word.lowercase().trim())
        saveParasiteWords(words)
    }
    
    fun removeParasiteWord(word: String) {
        val words = getParasiteWords().toMutableSet()
        words.remove(word.lowercase().trim())
        saveParasiteWords(words)
    }
    
    fun incrementReplacementCount() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())
        
        val statsJson = prefs.getString(REPLACEMENT_STATS, "{}")
        val type = object : TypeToken<Map<String, Int>>() {}.type
        val stats = gson.fromJson<Map<String, Int>>(statsJson, type)?.toMutableMap() ?: mutableMapOf()
        
        val total = stats["total"] ?: 0
        stats["total"] = total + 1
        
        val daily = stats[today] ?: 0
        stats[today] = daily + 1
        
        prefs.edit().putString(REPLACEMENT_STATS, gson.toJson(stats)).apply()
    }
    
    fun getTodayStats(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())
        
        val statsJson = prefs.getString(REPLACEMENT_STATS, "{}")
        val type = object : TypeToken<Map<String, Int>>() {}.type
        val stats = gson.fromJson<Map<String, Int>>(statsJson, type) ?: mapOf()
        
        return stats[today] ?: 0
    }
    
    fun getTotalStats(): Int {
        val statsJson = prefs.getString(REPLACEMENT_STATS, "{}")
        val type = object : TypeToken<Map<String, Int>>() {}.type
        val stats = gson.fromJson<Map<String, Int>>(statsJson, type) ?: mapOf()
        
        return stats["total"] ?: 0
    }
    
    fun isVibrationEnabled(): Boolean = prefs.getBoolean(VIBRATION_ENABLED, true)
    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(VIBRATION_ENABLED, enabled).apply()
    }
    
    fun isSoundEnabled(): Boolean = prefs.getBoolean(SOUND_ENABLED, true)
    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(SOUND_ENABLED, enabled).apply()
    }
    
    fun isAutoReplaceEnabled(): Boolean = prefs.getBoolean(AUTO_REPLACE, true)
    fun setAutoReplaceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(AUTO_REPLACE, enabled).apply()
    }
}
