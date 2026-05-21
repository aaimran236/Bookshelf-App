package com.example.bookshelfapp.network

data class VolumeInfo (
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val publisher: String?,
    val publishedDate: String?,
    val pageCount: Int?,
    val categories: List<String>?,
    val language: String?,
    val imageLinks: ImageLinks?
)