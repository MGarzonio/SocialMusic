package it.uninsubria.socialmusic

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var items = ArrayList<String>()
    private var listView: ListView? = null
    private var arrayAdapter : ArrayAdapter<String>? = null
    private var selectedItems = ArrayList<String>()
    private var type = ' '

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        listView = findViewById(R.id.items_ListView_List)
        type = intent.getCharExtra("type", ' ')
        var data = intent.getCharArrayExtra("data", " ")
        if(type == 'I')
            items = ArrayList(listOf(*resources.getStringArray(R.array.instruments)))
        else
            items =  ArrayList(listOf(*resources.getStringArray(R.array.genres)))
        if(selectedItems[0] != "none")
            loadSelection()
        selectedItems.clear()
        arrayAdapter = ArrayAdapter(this, R.layout.list_row, items)
        listView?.adapter = arrayAdapter
        listView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView?.onItemClickListener = this
    }

    private fun loadSelection(){
        for(s : String in selectedItems){
            if(s in items) {
                listView?.setSelection(items.indexOf(s))
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position) as String
        if(item in selectedItems){
            selectedItems.remove(item)
        } else {
            selectedItems.add(item)
        }
    }

    fun confirmChange(view: View) {
        var res = ""
        for(s : String in selectedItems)
            res += "$s,"
        val uid = FirebaseAuth.getInstance().currentUser.uid
        val ref = if(type == 'I')
            FirebaseDatabase.getInstance().getReference("users/$uid/instruments")
        else
            FirebaseDatabase.getInstance().getReference("users/$uid/genres")
        ref.setValue(res)
        Toast.makeText(view.context, getString(R.string.changes_saved), Toast.LENGTH_LONG).show()
        finish()
    }
}
