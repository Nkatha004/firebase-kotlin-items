package com.example.bityarn

data class Item(
    val id: Int = 0,
    val name: String = "",
    val location: String = "",
    val type: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val length: Int = 0,
    val status: Int = 0
) {
    // Empty constructor for Firebase
    constructor() : this(0, "", "", "", 0, 0, 0, 0)
}