package com.example.dictionarycore

/**
 * Fast word lookup structure
 * Time complexity: O(length of word)
 */
class LexiTrie {

    private val root = LexiNode()

    /** Insert a word into dictionary */
    fun insert(word: String, pointer: Int) {
        var node = root

        for (char in word.lowercase()) {
            node = node.children.getOrPut(char) { LexiNode() }
        }

        node.isWord = true
        node.pointer = pointer
    }

    /** Search for a word */
    fun search(word: String): Int? {
        var node = root

        for (char in word.lowercase()) {
            node = node.children[char] ?: return null
        }

        return if (node.isWord) node.pointer else null
    }
}