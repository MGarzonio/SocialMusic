package it.uninsubria.socialmusic.chat

class ChatMessage(val id: String, val text: String, val fromID: String, val toID:String, val timestamp: Long){
    constructor(): this("","","","",-1)
}