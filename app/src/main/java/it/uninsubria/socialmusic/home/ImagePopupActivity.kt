package it.uninsubria.socialmusic.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.uninsubria.socialmusic.R

class ImagePopupActivity : AppCompatActivity() {

    private lateinit var profilePhoto : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_popup)
        val uid = intent.getStringExtra("userID")!!
        profilePhoto = findViewById(R.id.popUp_imageView)
        loadPhoto(uid)
    }

    private fun loadPhoto(uid: String) {
        val res = FirebaseDatabase.getInstance().getReference("users/$uid")

        res.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                if (user.profile_image_url != "default") {
                    Glide.with(profilePhoto).load(user.profile_image_url).into(profilePhoto)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
