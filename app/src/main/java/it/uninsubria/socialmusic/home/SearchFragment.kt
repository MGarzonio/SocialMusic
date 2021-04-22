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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    val defaultID = "6N9HD0c5WgPsakocjfluSiSI0hm2"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewVal = inflater.inflate(R.layout.fragment_search, container, false) as View
        val btnSearch = viewVal.findViewById(R.id.search_button_search) as Button
        val instrumentTextView = viewVal.findViewById(R.id.instruments_textView_search) as TextView
        nameKey = viewVal.findViewById(R.id.name_editText_search) as EditText
        instrumentKey = viewVal.findViewById(R.id.instrument_Spinner_search) as Spinner
        genreKey = viewVal.findViewById(R.id.genre_Spinner_search) as Spinner
        recyclerView = viewVal.findViewById(R.id.user_recyclerView_search) as RecyclerView

        val instrumentAdapter = ArrayAdapter(viewVal.context, android.R.layout.simple_spinner_item, getInstruments())
        instrumentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val genresAdapter = ArrayAdapter(viewVal.context, android.R.layout.simple_spinner_item, getGenres())
        genresAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        instrumentKey.adapter = instrumentAdapter
        genreKey.adapter = genresAdapter

        btnSearch.setOnClickListener(this)

        instrumentKey?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                instrumentTextView.text = getString(R.string.musical_instruments)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                instrumentTextView.text = instrumentKey.selectedItem.toString()
            }
        }
        genreKey?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
               // genreTextView.text = getString(R.string.musical_genres)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               // genreTextView.text = instrumentKey.selectedItem.toString()
            }
        }

        fetchUsers(viewVal)

        return viewVal
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search_button_search -> doSearch()
        }
    }

    private fun getInstruments(): ArrayList<String> {
        var list = ArrayList<String>()

        list.add("Drum")
        list.add("Guitar")
        list.add("Bass")

        // TODO(get Array<String> instruments)

        return list
    }

    private fun getGenres(): ArrayList<String> {
        var list = ArrayList<String>()

        list.add("Rock")
        list.add("Metal")
        list.add("Jazz")

        // TODO(get Array<String> instruments)

        return list
    }

    private fun fetchUsers(view: View) {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val myUid = FirebaseAuth.getInstance().uid
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null && user.uid != myUid && user.uid != defaultID) {
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

    private fun doSearch() {
        // TODO(do research with filters on firebase)
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
