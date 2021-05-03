package it.uninsubria.socialmusic.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
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

class HomeFragment : Fragment() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    val userID = FirebaseAuth.getInstance().currentUser.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewVal = inflater.inflate(R.layout.fragment_home, container, false) as View
        val recyclerView = viewVal.findViewById<RecyclerView>(R.id.recyclerView_home)
        listenForPosts(viewVal)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        if (adapter == null) {
            Toast.makeText(viewVal.context, getString(R.string.no_messages), Toast.LENGTH_SHORT).show()
        }
        val addButton = viewVal.findViewById<Button>(R.id.add_button_home)
        addButton.setOnClickListener {
            createPost()
        }
        return viewVal
    }

    private fun listenForPosts(view: View){
        val ref = FirebaseDatabase.getInstance().getReference("/posts")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val post = snapshot.getValue(HomePost::class.java) ?: return
                adapter.add(PostRow(post))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val post = snapshot.getValue(HomePost::class.java) ?: return
                adapter.add(PostRow(post))

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun createPost(){
        val ref = FirebaseDatabase.getInstance().getReference("/posts").push()
        val post = HomePost(ref.key!!,"ciao a tutti", userID, System.currentTimeMillis()/1000, 1, 5)
        ref.setValue(post)
                .addOnSuccessListener {
                    recyclerView_home.scrollToPosition(adapter.itemCount -1)
                }

    }
}