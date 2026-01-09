package com.example.verlize

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.verlize.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var adapter: ParasiteWordsAdapter
    private lateinit var prefs: SharedPrefManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefs = SharedPrefManager.getInstance(this)
        
        setupUI()
        setupListeners()
        loadSettings()
    }
    
    private fun setupUI() {
        adapter = ParasiteWordsAdapter(
            words = prefs.getParasiteWords().toList(),
            onDeleteClick = { word ->
                prefs.removeParasiteWord(word)
                adapter.updateWords(prefs.getParasiteWords().toList())
                updateStatistics()
            }
        )
        
        binding.recyclerViewParasiteWords.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewParasiteWords.adapter = adapter
        
        val allWords = SynonymDictionary.getAllWords()
        val arrayAdapter = ArrayAdapter(this, 
            android.R.layout.simple_dropdown_item_1line, allWords)
        binding.editTextNewWord.setAdapter(arrayAdapter)
    }
    
    private fun setupListeners() {
        binding.buttonAddWord.setOnClickListener {
            val newWord = binding.editTextNewWord.text.toString().trim()
            if (newWord.isNotEmpty()) {
                prefs.addParasiteWord(newWord)
                adapter.updateWords(prefs.getParasiteWords().toList())
                binding.editTextNewWord.text.clear()
                updateStatistics()
            }
        }
        
        binding.buttonWordCards.setOnClickListener {
            startActivity(Intent(this, WordCardActivity::class.java))
        }
        
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.setVibrationEnabled(isChecked)
        }
        
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefs.setSoundEnabled(isChecked)
        }
        
        binding.switchAutoReplace.setOnCheckedChangeListener { _, isChecked ->
            prefs.setAutoReplaceEnabled(isChecked)
        }
        
        binding.buttonEnableKeyboard.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
            startActivity(intent)
        }
        
        updateStatistics()
    }
    
    private fun loadSettings() {
        binding.switchVibration.isChecked = prefs.isVibrationEnabled()
        binding.switchSound.isChecked = prefs.isSoundEnabled()
        binding.switchAutoReplace.isChecked = prefs.isAutoReplaceEnabled()
        
        if (prefs.getParasiteWords().isEmpty()) {
            binding.textViewNoWords.visibility = View.VISIBLE
        } else {
            binding.textViewNoWords.visibility = View.GONE
        }
    }
    
    private fun updateStatistics() {
        binding.textTodayStats.text = prefs.getTodayStats().toString()
        binding.textTotalStats.text = prefs.getTotalStats().toString()
        binding.textParasiteCount.text = prefs.getParasiteWords().size.toString()
    }
    
    override fun onResume() {
        super.onResume()
        updateStatistics()
        adapter.updateWords(prefs.getParasiteWords().toList())
    }
}

class ParasiteWordsAdapter(
    private var words: List<String>,
    private val onDeleteClick: (String) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<ParasiteWordsAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val textViewWord: android.widget.TextView = view.findViewById(R.id.textViewWord)
        val buttonDelete: android.widget.Button = view.findViewById(R.id.buttonDelete)
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.word_item, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        holder.textViewWord.text = word
        
        val synonyms = SynonymDictionary.getSynonyms(word)
        if (synonyms.isNotEmpty()) {
            holder.textViewWord.append("\nСинонимы: ${synonyms.take(2).joinToString(", ")}")
        }
        
        holder.buttonDelete.setOnClickListener {
            onDeleteClick(word)
        }
    }
    
    override fun getItemCount(): Int = words.size
    
    fun updateWords(newWords: List<String>) {
        words = newWords
        notifyDataSetChanged()
    }
}
