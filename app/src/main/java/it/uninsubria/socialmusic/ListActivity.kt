package it.uninsubria.socialmusic

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.list_recyclerview_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class ListActivity : AppCompatActivity() {

    private var adapter = GroupAdapter<GroupieViewHolder>()
    private var items = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        var type = intent.getCharExtra("type", ' ')
        items = if(type == 'I'){
            ArrayList(listOf(*resources.getStringArray(R.array.instruments)))
        }
        else{
            ArrayList(listOf(*resources.getStringArray(R.array.genres)))
        }
        loadAdapter()
        listView.adapter = adapter
    }

    private fun loadAdapter(){
        for(s in items){
            adapter.add(ListItem(s))
        }
    }

}

class ListItem(val name : String) : Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.itemTextView.text = name
    }

    override fun getLayout(): Int {
        return R.layout.list_recyclerview_row
    }
}