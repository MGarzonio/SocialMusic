package it.uninsubria.socialmusic

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
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

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val v = viewHolder.itemView
        v.chat_button_post.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_send_24, 0, 0);
        v.post_textView_post.text = post.text
        v.like_textView_post.text = post.like.toString()
        v.notLike_textView_post.text = post.unlike.toString()
        v.chat_button_post.setOnClickListener {
            val intent = Intent(it.context, ChatActivity::class.java)
            intent.putExtra(ChatFragment.USER_KEY, postFromUser)
            startActivity(it.context, intent,null)
        }

        val refUser = FirebaseDatabase.getInstance().getReference("/users/${post.fromID}")
        refUser.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postFromUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.user_textView_post.text = postFromUser?.username
                val target = viewHolder.itemView.user_imageView_post
                val imageUrl = postFromUser?.profile_image_url
                if(imageUrl != "default") {
                    Picasso.get().load(imageUrl).into(target)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.home_post_row
    }
}