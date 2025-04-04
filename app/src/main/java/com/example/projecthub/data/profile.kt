package com.example.projecthub.data

data class profile(
val name: String = "",
val bio: String = "",
val collegeName: String = "",
val semester: String = "",
val collegeLocation: String = "",
val skills: List<String> = emptyList()
)

