package com.example.verlize

class SynonymDictionary {
    
    companion object {
        private val synonymMap = mapOf(
            // Русские слова-паразиты
            "типа" to listOf("например", "вроде", "как бы", "словно"),
            "как бы" to listOf("будто", "словно", "как будто", "похоже"),
            "короче" to listOf("одним словом", "итак", "следовательно", "в общем"),
            "вот" to listOf("здесь", "тут", "смотрите", "обратите внимание"),
            "это" to listOf("данный", "упомянутый", "рассматриваемый"),
            "так сказать" to listOf("скажем так", "грубо говоря", "условно"),
            "просто" to listOf("легко", "элементарно", "несложно", "прямо"),
            "прямо" to listOf("непосредственно", "точно", "именно"),
            "на самом деле" to listOf("в действительности", "фактически", "действительно"),
            "вообще" to listOf("в целом", "в общем", "в принципе", "обычно"),
            "значит" to listOf("следовательно", "таким образом", "итак"),
            "понимаешь" to listOf("понимаете", "видишь", "видите"),
            "слушай" to listOf("послушай", "послушайте", "извини"),
            "блин" to listOf("черт", "ого", "ничего себе", "удивительно"),
            "прикольно" to listOf("интересно", "забавно", "любопытно", "здорово"),
            "круто" to listOf("отлично", "прекрасно", "великолепно", "замечательно"),
            "жесть" to listOf("ужас", "кошмар", "страшно", "пугающе"),
            "реально" to listOf("действительно", "на самом деле", "фактически"),
            "офигенно" to listOf("изумительно", "потрясающе", "восхитительно"),
            "норм" to listOf("нормально", "хорошо", "приемлемо", "удовлетворительно"),
            
            // Английские слова-паразиты
            "like" to listOf("such as", "for example", "similar to", "as if"),
            "actually" to listOf("in fact", "really", "truly", "indeed"),
            "basically" to listOf("essentially", "fundamentally", "simply"),
            "seriously" to listOf("truly", "honestly", "genuinely"),
            "literally" to listOf("exactly", "precisely", "verbatim"),
            "just" to listOf("simply", "merely", "only", "exactly"),
            "really" to listOf("truly", "genuinely", "very", "extremely"),
            "very" to listOf("extremely", "highly", "exceedingly", "immensely"),
            "so" to listOf("therefore", "thus", "hence", "consequently"),
            "well" to listOf("good", "fine", "okay", "alright"),
            "okay" to listOf("alright", "acceptable", "satisfactory", "fine"),
            "maybe" to listOf("perhaps", "possibly", "potentially"),
            "probably" to listOf("likely", "presumably", "doubtless"),
            "awesome" to listOf("amazing", "wonderful", "fantastic", "excellent"),
            "cool" to listOf("great", "nice", "pleasant", "agreeable"),
            "stuff" to listOf("things", "items", "objects", "materials"),
            "thing" to listOf("item", "object", "element", "piece"),
            "whatever" to listOf("anything", "whichever", "no matter what"),
            "anyway" to listOf("regardless", "nevertheless", "in any case")
        )
        
        fun getSynonyms(word: String): List<String> {
            return synonymMap[word.lowercase()] ?: emptyList()
        }
        
        fun getRandomSynonym(word: String): String? {
            val synonyms = getSynonyms(word)
            return if (synonyms.isNotEmpty()) synonyms.random() else null
        }
        
        fun hasSynonyms(word: String): Boolean {
            return synonymMap.containsKey(word.lowercase())
        }
        
        fun getAllWords(): List<String> {
            return synonymMap.keys.toList()
        }
    }
}
