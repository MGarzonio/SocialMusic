package it.uninsubria.socialmusic

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.list_recycleview_row.view.*

class ListActivity : AppCompatActivity() {

    private var adapter = GroupAdapter<GroupieViewHolder>()
    //private val user = Firebase.auth.currentUser.uid
    private var items = ArrayList<String>()
    private var images = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        var type = intent.getCharExtra("type",' ')
        if(type == 'I') getUserInstruments()
        else if(type == 'G') getUserGenres()
        loadAdapter()
        listView.adapter = adapter
    }

    private fun loadAdapter(){
        for(s in items){
            adapter.add(ListItem(s))
        }
    }

    private fun getUserInstruments() {
        // TODO(get instruments to firebase)

        items.add("Drum")
        items.add("Guitar")
        items.add("Electric triangle")
    }

    private fun getUserGenres() {
        // TODO(get genres to firebase)

        items.add("Rock")
        items.add("Jazz")
        items.add("Metal")
    }
}

class ListItem(val name : String) : Item<GroupieViewHolder>(){

    constructor():this("")

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.itemTextView.text = name
    }

    override fun getLayout(): Int {
        return R.layout.list_recycleview_row
    }
}