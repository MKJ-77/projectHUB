package com.example.projecthub.data

data class Assignment(
    val title : String = "",
    val description : String = "",
    val subject : String = "",
    val deadline : String = "",
    val budget : String = "",
    val postedBy : String = "",
    val timestamp : Long = System.currentTimeMillis()
)
