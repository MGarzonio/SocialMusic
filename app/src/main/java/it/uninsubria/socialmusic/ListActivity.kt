package it.uninsubria.socialmusic

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
        adapter.setOnItemClickListener { item, _ ->
            val selection = item as ListItem
            if(selection.getTextViewColor() == ColorDrawable(getColor(android.R.color.white)))
                selection.setTextViewColor(getColor(android.R.color.holo_blue_light))
            else
                selection.setTextViewColor(getColor(android.R.color.white))
        }
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
        item.text = name
    }

    override fun getLayout(): Int {
        return R.layout.list_recyclerview_row
    }

    fun getTextViewColor() : Drawable {
        return item.background
    }

    fun setTextViewColor(color : Int){
        item.background = ColorDrawable(color)
    }
}