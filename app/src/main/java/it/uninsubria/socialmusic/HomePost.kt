package it.uninsubria.socialmusic

class HomePost (val id: String, val text: String, val fromID: String, val timestamp: Long, val like: Int, val unlike: Int){
    constructor(): this("","","",-1, 0, 0)
}