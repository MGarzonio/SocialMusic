package it.uninsubria.socialmusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import it.uninsubria.socialmusic.home.HomeActivity

class ListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var items = ArrayList<String>()
    private var listView: ListView? = null
    private var arrayAdapter : ArrayAdapter<String>? = null
    private var selectedItem = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        listView = findViewById(R.id.items_ListView_List)
        val type = intent.getCharExtra("type", ' ')
        items = if(type == 'I'){
            ArrayList(listOf(*resources.getStringArray(R.array.instruments)))
        }
        else{
            ArrayList(listOf(*resources.getStringArray(R.array.genres)))
        }
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items)
        listView?.adapter = arrayAdapter
        listView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView?.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position) as String
        if(item in selectedItem){
            selectedItem.remove(item)
        } else {
            selectedItem.add(item)
        }
    }

    fun confirmChange(view: View) {
        var res = ""
        for(s : String in selectedItem)
            res += "$s,"
        Toast.makeText(view.context, getString(R.string.changes_saved), Toast.LENGTH_LONG).show()
    }
}
