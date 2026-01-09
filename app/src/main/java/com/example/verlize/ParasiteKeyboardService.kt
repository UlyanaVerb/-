package com.example.verlize

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Toast

class ParasiteKeyboardService : InputMethodService(), 
    KeyboardView.OnKeyboardActionListener {
    
    private lateinit var keyboardView: KeyboardView
    private lateinit var keyboard: Keyboard
    private var capsLock = false
    
    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(
            R.layout.keyboard_layout, null
        ) as KeyboardView
        
        keyboard = Keyboard(this, R.xml.method)
        keyboardView.keyboard = keyboard
        keyboardView.setOnKeyboardActionListener(this)
        keyboardView.isPreviewEnabled = false
        
        return keyboardView
    }
    
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val inputConnection = currentInputConnection ?: return
        
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> {
                inputConnection.deleteSurroundingText(1, 0)
            }
            Keyboard.KEYCODE_SHIFT -> {
                capsLock = !capsLock
                keyboard.isShifted = capsLock
                keyboardView.invalidateAllKeys()
            }
            Keyboard.KEYCODE_DONE -> {
                inputConnection.sendKeyEvent(
                    KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
                )
            }
            else -> {
                val code = primaryCode.toChar()
                var character = code.toString()
                
                if (capsLock && Character.isLetter(code)) {
                    character = character.uppercase()
                }
                
                inputConnection.commitText(character, 1)
                
                if (character == " " || character == "\n") {
                    checkForParasiteWord(inputConnection)
                }
            }
        }
    }
    
    private fun checkForParasiteWord(inputConnection: InputConnection) {
        val prefs = SharedPrefManager.getInstance(this)
        if (!prefs.isAutoReplaceEnabled()) return
        
        val textBeforeCursor = inputConnection.getTextBeforeCursor(50, 0) ?: return
        val text = textBeforeCursor.toString()
        
        val words = text.split(" ", "\n", ",", ".", "!", "?")
        if (words.isNotEmpty()) {
            val lastWord = words.last().lowercase().trim()
            val parasiteWords = prefs.getParasiteWords()
            
            if (parasiteWords.contains(lastWord) || SynonymDictionary.hasSynonyms(lastWord)) {
                val synonyms = SynonymDictionary.getSynonyms(lastWord)
                if (synonyms.isNotEmpty()) {
                    showSynonymSuggestions(lastWord, synonyms)
                    
                    if (prefs.isAutoReplaceEnabled()) {
                        replaceLastWord(words.last(), synonyms.random(), inputConnection)
                        prefs.incrementReplacementCount()
                        
                        if (prefs.isVibrationEnabled()) {
                            keyboardView.performHapticFeedback()
                        }
                    }
                }
            }
        }
    }
    
    private fun showSynonymSuggestions(word: String, synonyms: List<String>) {
        Toast.makeText(
            this, 
            "Для \"$word\": ${synonyms.take(3).joinToString(", ")}", 
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun replaceLastWord(oldWord: String, newWord: String, inputConnection: InputConnection) {
        inputConnection.deleteSurroundingText(oldWord.length, 0)
        inputConnection.commitText(newWord, 1)
        inputConnection.commitText(" ", 1)
    }
    
    override fun swipeRight() {}
    override fun swipeLeft() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {}
    override fun updateShiftKeyState() {}
}
