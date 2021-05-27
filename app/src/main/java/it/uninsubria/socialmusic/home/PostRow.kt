package it.uninsubria.socialmusic.home

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.chat.ChatActivity
import kotlinx.android.synthetic.main.home_post_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class PostRow(private val post: HomePost): Item<GroupieViewHolder>(){
    var postFromUser: User? = null
    private var currentUserID: String? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val delButton = viewHolder.itemView.findViewById<Button>(R.id.delete_button_post)
        if(currentUserID == post.fromID){
            delButton.isClickable = true
            delButton.alpha = 1f
        } else {
            delButton.isClickable = false
            delButton.alpha = 0f
        }
        val v = viewHolder.itemView
        v.chat_button_post.setCompoundDrawablesWithIntrinsicBounds(0, 0,
            R.drawable.ic_baseline_send_24, 0);
        v.post_textView_post.text = post.text
        v.like_textView_post.text = post.like.toString()
        v.notLike_textView_post.text = post.dislike.toString()
        v.time_textView_post.text = getDateFromTimestamp(post.timestamp)

        val refUser = FirebaseDatabase.getInstance().getReference("/users/${post.fromID}")
        refUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postFromUser = snapshot.getValue(User::class.java)
                v.user_textView_post.text = postFromUser?.username
                val target = v.user_imageView_post
                val target2 = v.user_textView_post
                val imageUrl = postFromUser?.profile_image_url
                if (imageUrl != "default") {
                    Glide.with(v.context).load(imageUrl).into(target)
                    target.setOnClickListener {
                        val intent = Intent(it.context, ImagePopupActivity::class.java)
                        intent.putExtra("userID", postFromUser!!.uid)
                        startActivity(it.context, intent, null)
                    }
                }
                target2.setOnClickListener {
                    val intent = Intent(it.context, UsersProfileActivity::class.java)
                    intent.putExtra("userID", postFromUser!!.uid)
                    startActivity(it.context, intent, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("POST:", "Post deleted!!!")
            }
        })
        v.chat_button_post.setOnClickListener {
            if(currentUserID != postFromUser!!.uid){
                val intent = Intent(it.context, ChatActivity::class.java)
                intent.putExtra(ChatFragment.USER_KEY, postFromUser)
                startActivity(it.context, intent, null)
            } else {
                Toast.makeText(it.context, it.context.getString(R.string.post_yours), Toast.LENGTH_SHORT).show()
            }
        }
        v.like_button_post.setOnClickListener {
            checkLike(v, "liked")
        }
        v.notLike_button_post.setOnClickListener {
            checkLike(v, "disliked")
        }
        v.delete_button_post.setOnClickListener {
            openPopup(it)
        }
    }

    override fun getLayout(): Int {
        return R.layout.home_post_row
    }

    private fun checkLike(view: View, type: String){
        var child = ""
        when(type){
            "liked" ->
                child = "postlike"
            "disliked" ->
                child = "postdislike"
        }
        val ref = FirebaseDatabase.getInstance().getReference("/$child/${post.id}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                snapshot.children.forEach {
                    if(it.value == currentUserID){
                        Toast.makeText(view.context, "Already $type!", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                addLikeOnPost(ref, type)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addLikeOnPost(refGiven: DatabaseReference, type: String){
        val refPost = FirebaseDatabase.getInstance().getReference("/posts/${post.id}")
        when(type){
            "liked" ->
                refPost.child("like").setValue(post.like + 1)
            "disliked" ->
                refPost.child("dislike").setValue(post.dislike + 1)
        }
        refGiven.push().setValue(currentUserID)
        checkOppositeLike(refPost, type)
    }

    private fun checkOppositeLike(refGiven: DatabaseReference, type: String){
        var child = ""
        when(type){
            "liked" ->
                child = "postdislike"
            "disliked" ->
                child = "postlike"
        }
        val refLike = FirebaseDatabase.getInstance().getReference("/$child/${post.id}")
        refLike.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                snapshot.children.forEach {
                    if(it.value == currentUserID){
                        when(type){
                            "liked" ->
                                refGiven.child("dislike").setValue(post.dislike - 1)
                            "disliked" ->
                                refGiven.child("like").setValue(post.like - 1)
                        }
                        refLike.child(it.key!!).removeValue()
                        return
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun openPopup(view: View){
        val builder = AlertDialog.Builder(view.context)
        builder.setIcon(R.drawable.ic_round_warning_24)
        builder.setTitle(view.context.getString(R.string.alert_title))
        builder.setMessage(view.context.getString(R.string.r_u_sure))
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            FirebaseDatabase.getInstance().getReference("/posts/${post.id}").removeValue()
            FirebaseDatabase.getInstance().getReference("/postlike/${post.id}").removeValue()
            FirebaseDatabase.getInstance().getReference("/postdislike/${post.id}").removeValue()
        }
        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(view.context, android.R.string.no, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
    private fun getDateFromTimestamp(timestamp: Long): String{
        val sdf = SimpleDateFormat.getDateTimeInstance()
        val time = timestamp * 1000
        val netDate = Date(time)
        return sdf.format(netDate)
    }
}