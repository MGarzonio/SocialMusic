package it.uninsubria.socialmusic.chat

import android.content.Intent
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.uninsubria.socialmusic.ImagePopupActivity
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.User
import kotlinx.android.synthetic.main.latestmessage_row.view.*

class LatestMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>(){

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lastMess_textView_latestMessage.text = chatMessage.text
        val chatPartnerID: String
        if(chatMessage.fromID == FirebaseAuth.getInstance().uid){
            chatPartnerID = chatMessage.toID
        }else{
            chatPartnerID = chatMessage.fromID
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerID")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.user_textView_latestMessage.text = chatPartnerUser?.username
                val target = viewHolder.itemView.user_imageView_latestMessage
                val imageUrl = chatPartnerUser?.profile_image_url
                if(imageUrl != "default") {
                    Glide.with(viewHolder.itemView.context).load(imageUrl).into(target)
                    //Picasso.get().load(imageUrl).into(target)
                    target.setOnClickListener {
                        val intent = Intent(it.context, ImagePopupActivity::class.java)
                        intent.putExtra("userID", chatPartnerUser!!.uid)
                        ContextCompat.startActivity(it.context, intent, null)
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latestmessage_row
    }
}