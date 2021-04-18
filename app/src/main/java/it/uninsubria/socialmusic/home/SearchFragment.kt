package it.uninsubria.socialmusic.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.User
import it.uninsubria.socialmusic.chat.ChatActivity
import kotlinx.android.synthetic.main.user_row.view.*

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewVal = inflater.inflate(R.layout.fragment_search, container, false) as View
        recyclerView = viewVal.findViewById<RecyclerView>(R.id.userSearch_recyclerView)
        fetchUsers(viewVal)
        return  viewVal
    }

    private fun fetchUsers(view: View) {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener{item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatActivity::class.java)
                    intent.putExtra(ChatFragment.USER_KEY, userItem.user)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    class UserItem(val user: User): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.user_textView.text = user.username
            Picasso.get().load(user.profile_image_url).into(viewHolder.itemView.circle_user_ImageView)
        }
        override fun getLayout(): Int {
            return R.layout.user_row
        }
    }

}