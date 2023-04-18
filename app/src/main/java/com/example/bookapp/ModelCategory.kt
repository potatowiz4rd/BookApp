package com.example.bookapp

class ModelCategory {
    var id = ""
    var category = ""
    var timestamp: Long? = null
    var uid = ""

    constructor() {}
    constructor(id: String, category: String, timestamp: Long?, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }
}