package com.example.dictionarycore

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

    /** Get all words starting with prefix */
    fun startsWith(prefix: String): List<String> {
        var node = root

        for (char in prefix.lowercase()) {
            node = node.children[char] ?: return emptyList()
        }

        val results = mutableListOf<String>()
        collectWords(node, prefix.lowercase(), results)
        return results
    }

    private fun collectWords(node: LexiNode, current: String, results: MutableList<String>) {
        if (node.isWord) results.add(current)

        for ((char, child) in node.children) {
            collectWords(child, current + char, results)
        }
    }

}