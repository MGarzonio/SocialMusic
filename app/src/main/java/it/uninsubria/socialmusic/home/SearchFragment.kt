package it.uninsubria.socialmusic.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.User
import it.uninsubria.socialmusic.chat.ChatActivity
import kotlinx.android.synthetic.main.user_row.view.*

class SearchFragment : Fragment(), View.OnClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var nameKey: EditText
    private lateinit var instrumentKey: Spinner
    private lateinit var genreKey: Spinner
    private var selectedInstrument = "None"
    private var selectedGenre = "None"

    val defaultID = "6N9HD0c5WgPsakocjfluSiSI0hm2"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewVal = inflater.inflate(R.layout.fragment_search, container, false) as View
        val btnSearch = viewVal.findViewById(R.id.search_button_search) as Button
        nameKey = viewVal.findViewById(R.id.name_editText_search) as EditText
        instrumentKey = viewVal.findViewById(R.id.instrument_Spinner_search) as Spinner
        genreKey = viewVal.findViewById(R.id.genre_Spinner_search) as Spinner
        recyclerView = viewVal.findViewById(R.id.user_recyclerView_search) as RecyclerView

        val instrumentAdapter = ArrayAdapter(viewVal.context, R.layout.color_spinner_layout, getInstruments())
        instrumentAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        val genresAdapter = ArrayAdapter(viewVal.context, R.layout.color_spinner_layout, getGenres())
        genresAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)

        instrumentKey.adapter = instrumentAdapter
        genreKey.adapter = genresAdapter

        btnSearch.setOnClickListener(this)

        instrumentKey?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedInstrument = instrumentKey.selectedItem.toString()
            }
        }
        genreKey?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(genreKey.selectedItem.toString() != "None")
                    selectedGenre = genreKey.selectedItem.toString()
            }
        }

        return viewVal
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search_button_search -> doSearch(v)
        }
    }

    private fun getInstruments(): ArrayList<String> {
        var list = ArrayList<String>()

        list.add("None")
        list.add("Drum")
        list.add("Guitar")
        list.add("Bass")

        return list
    }

    private fun getGenres(): ArrayList<String> {
        var list = ArrayList<String>()

        list.add("None")
        list.add("Rock")
        list.add("Metal")
        list.add("Jazz")

        return list
    }

    private fun fetchUsers(view: View, selectedName : String) {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val myUid = FirebaseAuth.getInstance().uid
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null && user.uid != myUid && user.uid != defaultID) {
                        if(selectedName != ""){
                            if(user.name == selectedName || user.surname == selectedName || user.username == selectedName)
                                adapter.add(UserItem(user))
                        } else
                            adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatActivity::class.java)
                    intent.putExtra(ChatFragment.USER_KEY, userItem.user)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun doSearch(view: View) {
        fetchUsers(view, nameKey.text.toString())
    }

    class UserItem(val user: User) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val target = viewHolder.itemView.circle_user_ImageView
            viewHolder.itemView.user_textView.text = user.username
            val imageUrl = user?.profile_image_url
            if(imageUrl != "default") {
                Picasso.get().load(imageUrl).into(target)
            }
        }

        override fun getLayout(): Int {
            return R.layout.user_row
        }
    }
}
