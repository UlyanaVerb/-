package com.example.verlize

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.verlize.databinding.ActivityWordCardBinding

class WordCardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityWordCardBinding
    private lateinit var prefs: SharedPrefManager
    private lateinit var adapter: WordCardAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefs = SharedPrefManager.getInstance(this)
        setupViewPager()
        setupListeners()
    }
    
    private fun setupViewPager() {
        val words = prefs.getParasiteWords().toList()
        adapter = WordCardAdapter(words)
        
        binding.viewPagerCards.adapter = adapter
        binding.viewPagerCards.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        
        binding.textCardNumber.text = "1/${words.size}"
        
        binding.viewPagerCards.registerOnPageChangeCallback(object : 
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.textCardNumber.text = "${position + 1}/${words.size}"
            }
        })
    }
    
    private fun setupListeners() {
        binding.buttonBack.setOnClickListener {
            finish()
        }
        
        binding.buttonNextCard.setOnClickListener {
            if (binding.viewPagerCards.currentItem < adapter.itemCount - 1) {
                binding.viewPagerCards.currentItem = binding.viewPagerCards.currentItem + 1
            } else {
                binding.viewPagerCards.currentItem = 0
            }
        }
    }
}

class WordCardAdapter(private val words: List<String>) : 
    androidx.recyclerview.widget.RecyclerView.Adapter<WordCardAdapter.ViewHolder>() {
    
    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val textViewWord: android.widget.TextView = view.findViewById(R.id.textViewWord)
        val textViewSynonyms: android.widget.TextView = view.findViewById(R.id.textViewSynonyms)
    }
    
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.word_card_item, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        
        holder.textViewWord.text = word
        
        val synonyms = SynonymDictionary.getSynonyms(word)
        if (synonyms.isNotEmpty()) {
            holder.textViewSynonyms.text = 
                "Синонимы:\n• ${synonyms.joinToString("\n• ")}"
        } else {
            holder.textViewSynonyms.text = "Синонимы не найдены"
        }
    }
    
    override fun getItemCount(): Int = words.size
}
