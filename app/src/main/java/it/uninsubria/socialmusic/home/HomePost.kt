package it.uninsubria.socialmusic.home

class HomePost (val id: String, val text: String, val fromID: String, val timestamp: Long, val like: Int, val dislike: Int){
    constructor(): this("","","",-1, 0, 0)
}