package it.uninsubria.socialmusic

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.uninsubria.socialmusic.chat.ChatActivity
import it.uninsubria.socialmusic.home.ChatFragment
import kotlinx.android.synthetic.main.home_post_row.view.*

class PostRow (private val post: HomePost): Item<GroupieViewHolder>(){
    var postFromUser: User? = null
    val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val delButton = viewHolder.itemView.findViewById<Button>(R.id.delete_button_post)
        if(currentUserID == post.fromID){
            delButton.isClickable = true
            delButton.alpha = 1f
        } else {
            delButton.isClickable = false
            delButton.alpha = 0f
        }
        val v = viewHolder.itemView
        v.chat_button_post.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_send_24, 0, 0);
        v.post_textView_post.text = post.text
        v.like_textView_post.text = post.like.toString()
        v.notLike_textView_post.text = post.unlike.toString()

        val refUser = FirebaseDatabase.getInstance().getReference("/users/${post.fromID}")
        refUser.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postFromUser = snapshot.getValue(User::class.java)
                v.user_textView_post.text = postFromUser?.username
                val target = v.user_imageView_post
                val imageUrl = postFromUser?.profile_image_url
                if(imageUrl != "default") {
                    Picasso.get().load(imageUrl).into(target)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        v.chat_button_post.setOnClickListener {
            if(currentUserID != postFromUser!!.uid){
                val intent = Intent(it.context, ChatActivity::class.java)
                intent.putExtra(ChatFragment.USER_KEY, postFromUser)
                startActivity(it.context, intent,null)
            } else {
                Toast.makeText(it.context, it.context.getString(R.string.post_yours), Toast.LENGTH_SHORT).show()
            }
        }
        v.like_button_post.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("/posts/${post.id}")
            ref.child("like").setValue(post.like + 1)
        }
        v.notLike_button_post.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("/posts/${post.id}")
            ref.child("unlike").setValue(post.unlike + 1)
        }
        v.delete_button_post.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("/posts/${post.id}")
            ref.removeValue()
        }
    }

    override fun getLayout(): Int {
        return R.layout.home_post_row
    }
}