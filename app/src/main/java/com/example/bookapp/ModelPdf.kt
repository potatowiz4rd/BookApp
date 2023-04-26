package com.example.bookapp

class ModelPdf {

    var uid: String = ""
    var id: String = ""
    var author: String = ""
    var categoryId: String = ""
    var url: String = ""
    var title: String = ""
    var description: String = ""
    var timestamp: Long = 0
    var viewsCount: Long = 0
    var downloadsCount: Long = 0

    constructor()

    constructor(
        uid: String,
        id: String,
        author: String,
        categoryId: String,
        url: String,
        title: String,
        description: String,
        timestamp: Long,
        viewsCount: Long,
        downloadsCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.author = author
        this.categoryId = categoryId
        this.url = url
        this.title = title
        this.description = description
        this.timestamp = timestamp
        this.viewsCount = viewsCount
        this.downloadsCount = downloadsCount
    }


}