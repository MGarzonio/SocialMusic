package it.uninsubria.socialmusic

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.list_recyclerview_row.view.*

class ListActivity : AppCompatActivity() {

    private var adapter = GroupAdapter<GroupieViewHolder>()
    private var items = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val type = intent.getCharExtra("type", ' ')
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

    private lateinit var item: TextView

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        item = viewHolder.itemView.itemTextView as TextView
        item.setOnClickListener {
            //if(item.background == ContextCompat.getDrawable(context, R.drawable.green_resource))
        }
        item.text = name
    }

    override fun getLayout(): Int {
        return R.layout.list_recyclerview_row
    }
}