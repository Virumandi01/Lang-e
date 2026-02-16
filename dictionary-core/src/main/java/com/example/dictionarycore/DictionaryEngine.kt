package com.example.dictionarycore

/**
 * Public API used by the app
 */
class DictionaryEngine {

    private val trie = LexiTrie()
    private val meanings = mutableMapOf<Int, String>()

    init {
        // Temporary demo words (we will remove later)
        addWord("print", "Python function used to output text")
        addWord("vector", "C++ dynamic array container from STL")
        addWord("class", "Blueprint for creating objects in OOP languages")
        addWord("list", "Python ordered mutable collection")
        addWord("public", "Java access modifier")
    }

    private fun addWord(word: String, meaning: String) {
        val pointer = meanings.size
        meanings[pointer] = meaning
        trie.insert(word, pointer)
    }

    fun search(word: String): String? {
        val pointer = trie.search(word) ?: return null
        return meanings[pointer]
    }
}
