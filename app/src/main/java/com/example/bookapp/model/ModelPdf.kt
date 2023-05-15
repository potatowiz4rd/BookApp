package com.example.bookapp.model

class ModelPdf {

    var uid: String = ""
    var id: String = ""
    var author: String = ""
    var categoryId: String = ""
    var url: String = ""
    var title: String = ""
    var audio: String = ""
    var description: String = ""
    var type: String = ""
    var image: String = ""
    var timestamp: Long = 0
    var viewsCount: Long = 0
    var downloadsCount: Long = 0
    var isFavourite = false

    constructor()

    constructor(
        uid: String,
        id: String,
        author: String,
        categoryId: String,
        url: String,
        title: String,
        audio: String,
        description: String,
        type: String,
        image: String,
        timestamp: Long,
        viewsCount: Long,
        downloadsCount: Long,
        isFavourite: Boolean
    ) {
        this.uid = uid
        this.id = id
        this.author = author
        this.categoryId = categoryId
        this.url = url
        this.title = title
        this.audio = audio
        this.description = description
        this.type = type
        this.image = image
        this.timestamp = timestamp
        this.viewsCount = viewsCount
        this.downloadsCount = downloadsCount
        this.isFavourite = isFavourite
    }


}