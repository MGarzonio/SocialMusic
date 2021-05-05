package it.uninsubria.socialmusic.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.from_chat_row.view.*
import kotlinx.android.synthetic.main.to_chat_row.view.*

class ChatActivity : AppCompatActivity() {

    var toUser: User? = null
    val adapter = GroupAdapter<GroupieViewHolder>()

    companion object{
        var currentUser: User? = null
        const val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chat_recyclerView.adapter = adapter
        toUser = intent.getParcelableExtra(USER_KEY)
        supportActionBar?.title = toUser?.username
        fetchCurrentUser()
        listenForMessages()
        send_button.setOnClickListener {
            sendMessage()
        }
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val fer = FirebaseDatabase.getInstance().getReference("/users/$uid")
        fer.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun listenForMessages() {
        val fromID = FirebaseAuth.getInstance().uid
        val toID = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage != null){
                    if(chatMessage.fromID == FirebaseAuth.getInstance().uid) {
                        val currentUser = currentUser
                        adapter.add(ChatToItem(chatMessage.text, currentUser!!))
                    }else{
                        adapter.add(ChatFromItem(chatMessage.text, toUser!!))
                    }
                }
                chat_recyclerView.scrollToPosition(adapter.itemCount -1)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val messageText = message_editText_chat.text.toString()
        val fromID = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(USER_KEY)
        val toID = user!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()
        val chatMessage = ChatMessage(ref.key!!, messageText, fromID!!, toID, System.currentTimeMillis()/1000)
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                message_editText_chat.text.clear()
                chat_recyclerView.scrollToPosition(adapter.itemCount -1)
            }
        toRef.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-message/$fromID/$toID")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-message/$toID/$fromID")
        latestMessageToRef.setValue(chatMessage)
    }
}

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_from_textView.text = text
        val photoUri = user.profile_image_url
        if(photoUri != "default") {
            val target = viewHolder.itemView.chat_from_imageView
            Glide.with(viewHolder.itemView.context).load(photoUri).into(target)
        }
    }
    override fun getLayout(): Int {
        return R.layout.from_chat_row
    }
}
class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_to_textView.text = text
        val photoUri = user.profile_image_url
        if(photoUri != "default") {
            val target = viewHolder.itemView.chat_to_imageView
            Glide.with(viewHolder.itemView.context).load(photoUri).into(target)
        }

    }
    override fun getLayout(): Int {
        return R.layout.to_chat_row
    }
}