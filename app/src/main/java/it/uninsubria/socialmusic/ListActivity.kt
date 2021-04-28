package it.uninsubria.socialmusic

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.properties.Delegates

class ListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var items : ArrayList<String>
    private lateinit var listView: ListView
    private lateinit var arrayAdapter : ArrayAdapter<String>
    private lateinit var selectedItems : ArrayList<String>
    private var type by Delegates.notNull<Char>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        selectedItems = ArrayList()
        type = intent.getCharExtra("type", ' ')
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        items = if (type == 'I') {
            ArrayList(listOf(*resources.getStringArray(R.array.instruments)))
        } else {
            ArrayList(listOf(*resources.getStringArray(R.array.genres)))
        }

        arrayAdapter = ArrayAdapter(this, R.layout.list_row, items)

        listView = findViewById(R.id.items_ListView_List)
        listView.adapter = arrayAdapter
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.onItemClickListener = this

        if (type == 'I') {
            loadData(uid, "instruments")
        } else {
            loadData(uid, "genres")
        }
    }

    private fun loadData(id: String, path: String) {
        val res = FirebaseDatabase.getInstance().getReference("users/$id").child(path)

        res.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loadSelection(snapshot.value as String)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun loadSelection(data : String) {
        val values = data.split(",")
        for(s : String in values) {
            if(s in items) {
                selectedItems.add(s)
            }
        }
        for (item: String in selectedItems) {
            listView.setItemChecked(items.indexOf(item), true)
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
        var ris = ""
        if (selectedItems.isEmpty()) {
            ris = "none"
        } else {
            for (s: String in selectedItems) {
                ris += "$s,"
            }
        }
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = if (type == 'I') {
            FirebaseDatabase.getInstance().getReference("users/$uid/instruments")
        } else {
            FirebaseDatabase.getInstance().getReference("users/$uid/genres")
        }
        ref.setValue(ris)
        Toast.makeText(view.context, getString(R.string.changes_saved), Toast.LENGTH_LONG).show()
        finish()
    }
}
