package com.example.bookshelfapp.fake

import com.example.bookshelfapp.network.BookItem
import com.example.bookshelfapp.network.ImageLinks
import com.example.bookshelfapp.network.SearchBooksResponse
import com.example.bookshelfapp.network.SpecificBookResponse
import com.example.bookshelfapp.network.VolumeInfo

object FakeDataSource {

    val fakeBookResponse= SearchBooksResponse(
        items = listOf(
            BookItem("id1"),
            BookItem("id2")
        )
    )

    val fakeBookInfoResponse= mapOf(
        "id1" to SpecificBookResponse(
            volumeInfo = VolumeInfo(
                imageLinks = ImageLinks(thumbnail = "https://url.com/id_1.jpg")
            )
        ),
        "id2" to SpecificBookResponse(
            volumeInfo = VolumeInfo(
                imageLinks = ImageLinks(thumbnail = "https://url.com/id_1.jpg")
            )
        )
    )

    val thumbnailList=listOf(
        "https://url.com/id_1.jpg",
        "https://url.com/id_1.jpg"
    )
}