package it.uninsubria.socialmusic.chat

import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.uninsubria.socialmusic.PopUpWindow
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.User
import kotlinx.android.synthetic.main.latestmessage_row.view.*

class LatestMessageRow(private val chatMessage: ChatMessage, val view: View): Item<GroupieViewHolder>(){

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
                    Picasso.get().load(imageUrl).into(target)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latestmessage_row
    }

    private fun showImagePopup(image: Button){
        image.setOnClickListener {
            val intent = Intent(view.context, PopUpWindow::class.java)
            intent.putExtra("popuptitle", "Error")
            intent.putExtra("popuptext", "Sorry, that email address is already used!")
            intent.putExtra("popupbtn", "OK")
            intent.putExtra("darkstatusbar", false)
            //startActivity(intent)
        }
    }
}