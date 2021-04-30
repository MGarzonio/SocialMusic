package it.uninsubria.socialmusic.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.chat.ChatActivity
import it.uninsubria.socialmusic.chat.ChatMessage
import it.uninsubria.socialmusic.chat.LatestMessageRow

class ChatFragment : Fragment() {
    companion object{
        val USER_KEY = "USER_KEY"
    }
    private val adapter = GroupAdapter<GroupieViewHolder>()
    val messagesMap = HashMap<String, ChatMessage>()
    val fromID = FirebaseAuth.getInstance().uid
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewVal = inflater.inflate(R.layout.fragment_chat, container, false) as View
        val recyclerView = viewVal.findViewById<RecyclerView>(R.id.latestMessage_recyclerView)
        listenForLatestMessages()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(view.context, ChatActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        if (adapter == null) {
            Toast.makeText(viewVal.context, getString(R.string.no_messages), Toast.LENGTH_SHORT).show()
        }
        return viewVal
    }

    private fun refreshRView(){
        adapter.clear()
        messagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/latest-message/$fromID")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return //elvis operator '?: return' instead of '!!'
                messagesMap[snapshot.key!!] = chatMessage
                    refreshRView()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                messagesMap[snapshot.key!!] = chatMessage
                refreshRView()
            }
            //Not used, but needed
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}