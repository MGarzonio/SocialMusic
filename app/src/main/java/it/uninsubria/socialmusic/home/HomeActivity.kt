package it.uninsubria.socialmusic.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.socialmusic.LoginActivity
import it.uninsubria.socialmusic.R
import it.uninsubria.socialmusic.R.layout.activity_home

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_home)
        verifyUserLoggedIn()
        val controllerNavigation = findNavController(R.id.fragmentView)
        val bottomView = findViewById<BottomNavigationView>(R.id.BottomMenu)

        bottomView.setupWithNavController(controllerNavigation)
    }
    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}