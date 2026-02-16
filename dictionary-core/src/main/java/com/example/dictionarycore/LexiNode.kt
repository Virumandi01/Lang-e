package com.example.dictionarycore

/**
 * A single character node in the word tree
 */
class LexiNode {

    // Next possible characters
    val children: MutableMap<Char, LexiNode> = mutableMapOf()

    // Points to meaning location in file (later)
    var pointer: Int = -1

    // Marks end of a valid word
    var isWord: Boolean = false
}