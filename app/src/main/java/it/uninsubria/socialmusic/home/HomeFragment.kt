package it.uninsubria.socialmusic.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import it.uninsubria.socialmusic.HomePost
import it.uninsubria.socialmusic.PostRow
import it.uninsubria.socialmusic.R
import kotlinx.android.synthetic.main.fragment_home.*
import org.w3c.dom.Text

class HomeFragment : Fragment() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    val postsMap = HashMap<String, HomePost>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewVal = inflater.inflate(R.layout.fragment_home, container, false) as View
        val recyclerView = viewVal.findViewById<RecyclerView>(R.id.recyclerView_home)
        listenForPosts(viewVal)
        recyclerView.adapter = adapter
        val addButton = viewVal.findViewById<Button>(R.id.add_button_home)
        val text = viewVal.findViewById<EditText>(R.id.message_editText_home)
        addButton.setOnClickListener {
            if(text.text.isNotEmpty()) {
                createPost(viewVal)
                text.text.clear()
            } else {
                Toast.makeText(viewVal.context, getString(R.string.write_something), Toast.LENGTH_SHORT).show()
            }
        }
        return viewVal
    }
    private fun refreshList(view: View){
        adapter.clear()
        postsMap.values.forEach{
            adapter.add(PostRow(it))
        }
        if(adapter.itemCount == 0){
            view.findViewById<TextView>(R.id.emoji_textView_nothing).alpha = 0.5f
            view.findViewById<TextView>(R.id.message_textView_nothing).alpha = 0.5f
            view.findViewById<RecyclerView>(R.id.recyclerView_home).alpha = 0f
        } else {
            view.findViewById<TextView>(R.id.emoji_textView_nothing).alpha = 0f
            view.findViewById<TextView>(R.id.message_textView_nothing).alpha = 0f
            view.findViewById<RecyclerView>(R.id.recyclerView_home).alpha = 1f
        }
    }
    private fun listenForPosts(view: View){
        val ref = FirebaseDatabase.getInstance().getReference("/posts")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val post = snapshot.getValue(HomePost::class.java) ?: return
                postsMap[snapshot.key!!] = post
                refreshList(view)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val post = snapshot.getValue(HomePost::class.java) ?: return
                postsMap[snapshot.key!!] = post
                refreshList(view)

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                postsMap.remove(snapshot.key)
                refreshList(view)
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun createPost(view: View){
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/posts").push()
        val message = view.findViewById<EditText>(R.id.message_editText_home).text.toString()
        val post = HomePost(ref.key!!, message, userID, System.currentTimeMillis()/1000, 0, 0)
        ref.setValue(post)
                .addOnSuccessListener {
                    recyclerView_home!!.scrollToPosition(adapter.itemCount -1)
                }

    }
}