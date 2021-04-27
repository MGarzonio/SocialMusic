package it.uninsubria.socialmusic.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import it.uninsubria.socialmusic.LoginActivity
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.R.layout.activity_home
import it.uninsubria.socialmusic.User

class HomeActivity : AppCompatActivity() {

    var currentUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_home)
        verifyUserLoggedIn()
        fetchCurrentUser()
        val controllerNavigation = findNavController(R.id.fragmentView)
        val bottomView = findViewById<BottomNavigationView>(R.id.BottomMenu)

        bottomView.setupWithNavController(controllerNavigation)
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val fer = FirebaseDatabase.getInstance().getReference("/users/$uid")
        fer.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null || !Firebase.auth.currentUser.isEmailVerified){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}