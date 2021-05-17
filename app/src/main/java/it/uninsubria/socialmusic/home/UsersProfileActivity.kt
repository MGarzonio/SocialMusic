package it.uninsubria.socialmusic.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.chat.ChatActivity
import it.uninsubria.socialmusic.chat.ChatActivity.Companion.USER_KEY
import java.lang.Integer.parseInt

class UsersProfileActivity : AppCompatActivity() {

    private lateinit var genres : Spinner
    private lateinit var instrument: Spinner
    private lateinit var profilePhoto : de.hdodenhof.circleimageview.CircleImageView
    private lateinit var username : EditText
    private lateinit var name : EditText
    private lateinit var surname : EditText
    private lateinit var location : EditText
    private var user : User? = null
    private var selectedID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_profile)
        val uid = intent.getStringExtra("userID")!!

        genres = findViewById(R.id.gen_spinner_UsersProfile)
        instrument = findViewById(R.id.instrument_spinner_UsersProfile)
        profilePhoto = findViewById(R.id.profilePhoto_imageView_UsersProfile)
        username = findViewById(R.id.nickname_editText_UsersProfile)
        name = findViewById(R.id.name_editText_UsersProfile)
        surname = findViewById(R.id.surname_editText_UsersProfile)
        location = findViewById(R.id.location_editText_UsersProfile)

        loadProfile(uid)
    }

    private fun loadProfile(uid: String){
        val res = FirebaseDatabase.getInstance().getReference("users/$uid")

        res.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)!!
                if (user!!.profile_image_url != "default") {
                    Glide.with(applicationContext).load(user!!.profile_image_url).into(profilePhoto)
                }
                name.setText(user!!.name)
                username.setText(user!!.username)
                surname.setText(user!!.surname)
                location.setText(user!!.location)
                selectedID = user!!.uid
                setSpinner()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setSpinner(){
        val instrumentAdapter = ArrayAdapter(this, R.layout.color_spinner_layout, getUserInstruments())
        instrumentAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        val genresAdapter = ArrayAdapter(this, R.layout.color_spinner_layout, getUserGenres())
        genresAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        genres.adapter = genresAdapter
        instrument.adapter = instrumentAdapter
    }

    private fun getUserGenres() : ArrayList<String> {
        val list = user!!.genres.split(",")
        val genresList = ArrayList(listOf(*resources.getStringArray(R.array.genres)))
        val arrayList = ArrayList<String>()
        for (index in list)
            if (index.isNotEmpty()) {
                if (index != "none")
                    arrayList.add(genresList[parseInt(index)])
                else
                    arrayList.add(resources.getString(R.string.none))
            }
        return arrayList
    }

    private fun getUserInstruments() : ArrayList<String> {
        val list = user!!.instruments.split(",")
        val instrumentList = ArrayList(listOf(*resources.getStringArray(R.array.instruments)))
        val arrayList = ArrayList<String>()
        for (index in list)
            if (index.isNotEmpty()) {
                if (index != "none")
                    arrayList.add(instrumentList[parseInt(index)])
                else
                    arrayList.add(resources.getString(R.string.none))
            }
        return arrayList
    }

    fun openChat(view: View) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(USER_KEY, user)
        startActivity(intent)
        finish()
    }

    fun openMaps(view: View) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("city", location.text.toString())
        intent.putExtra("nickname", username.text.toString())
        startActivity(intent)
    }

}