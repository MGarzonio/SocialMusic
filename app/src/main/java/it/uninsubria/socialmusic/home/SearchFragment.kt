package it.uninsubria.socialmusic.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.User
import it.uninsubria.socialmusic.UsersProfileActivity
import it.uninsubria.socialmusic.chat.ChatActivity
import kotlinx.android.synthetic.main.user_row.view.*
import java.lang.Integer.parseInt
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment(), View.OnClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var nameKey: EditText
    private lateinit var instrumentKey: Spinner
    private lateinit var genreKey: Spinner
    private lateinit var genresList: ArrayList<String>
    private lateinit var instrumentList: ArrayList<String>
    private lateinit var monkey: TextView
    private lateinit var textNothing: TextView
    private var viewAll = true
    private var selectedInstrument = -1
    private var selectedGenre = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewVal = inflater.inflate(R.layout.fragment_search, container, false) as View
        val btnSearch = viewVal.findViewById(R.id.search_button_search) as Button
        nameKey = viewVal.findViewById(R.id.name_editText_search) as EditText
        instrumentKey = viewVal.findViewById(R.id.instrument_Spinner_search) as Spinner
        genreKey = viewVal.findViewById(R.id.genre_Spinner_search) as Spinner
        recyclerView = viewVal.findViewById(R.id.user_recyclerView_search) as RecyclerView
        monkey = viewVal.findViewById(R.id.emoji_textView_search) as TextView
        textNothing = viewVal.findViewById(R.id.message_textView_search) as TextView
        instrumentList = ArrayList(listOf(*resources.getStringArray(R.array.instruments)))
        instrumentList.add(0, resources.getString(R.string.all))
        genresList = ArrayList(listOf(*resources.getStringArray(R.array.genres)))
        genresList.add(0, resources.getString(R.string.all))

        val instrumentAdapter = ArrayAdapter(viewVal.context, R.layout.color_spinner_layout, instrumentList)
        instrumentAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        val genresAdapter = ArrayAdapter(viewVal.context, R.layout.color_spinner_layout, genresList)
        genresAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)

        instrumentKey.adapter = instrumentAdapter
        genreKey.adapter = genresAdapter

        btnSearch.setOnClickListener(this)

        instrumentKey.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedInstrument = -1
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedInstrument = instrumentKey.selectedItemPosition - 1
            }
        }
        genreKey.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedGenre = -1
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedGenre = genreKey.selectedItemPosition - 1
            }
        }

        fetchUsers(viewVal)

        return viewVal
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search_button_search -> {
                fetchUsers(v)
                viewAll = false
            }
        }
    }

    private fun fetchUsers(view: View) {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val myUid = FirebaseAuth.getInstance().uid
        val name = nameKey.text.toString().toLowerCase(Locale.ROOT)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null && user.uid != myUid && user.verified == "yes") {
                        if (viewableUser(user, name)) {
                            adapter.add(UserItem(user))
                        }
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatActivity::class.java)
                    intent.putExtra(ChatFragment.USER_KEY, userItem.user)
                    startActivity(intent)
                }
                if(adapter.itemCount == 0){
                    monkey.alpha = 0.5f
                    textNothing.alpha = 0.5f
                    recyclerView.alpha = 0f
                } else {
                    monkey.alpha = 0f
                    textNothing.alpha = 0f
                    recyclerView.alpha = 1f
                }
                recyclerView.adapter = adapter
                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, UsersProfileActivity::class.java)
                    val selectedUser = item as UserItem
                    intent.putExtra("userID", selectedUser.user.uid)
                    viewAll = true
                    startActivity(intent)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun viewableUser(user: User, selectedName: String): Boolean {
        val genreCheck =
                if (selectedGenre == -1) true
                else inUserList(genresList[selectedGenre], user.genres, genresList)
        val instrumentCheck =
                if (selectedInstrument == -1) true
                else inUserList(instrumentList[selectedInstrument], user.instruments, instrumentList)
        val nameCheck =
                if (selectedName.isEmpty()) true
                else selectedName == user.name.toLowerCase(Locale.ROOT) || selectedName == user.username.toLowerCase(Locale.ROOT) || selectedName == user.surname.toLowerCase(Locale.ROOT)
        return genreCheck && instrumentCheck && nameCheck
    }

    private fun inUserList(key: String, list: String, items: ArrayList<String>) : Boolean {
        val indexList = list.split(",")
        if (indexList[0] == "none" || indexList.isEmpty() || indexList[0] == "") {
            return false
        }
        for (i in indexList) {
            if(i != "") {
                if (key == items[parseInt(i)]) {
                    return true
                }
            }
        }
        return false
    }

    class UserItem(val user: User) : Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val target = viewHolder.itemView.circle_user_ImageView
            viewHolder.itemView.user_textView.text = user.username
            val imageUrl = user.profile_image_url
            if (imageUrl != "default") {
                Picasso.get().load(imageUrl).into(target)
            }
        }

        override fun getLayout(): Int {
            return R.layout.user_row
        }
    }
}
