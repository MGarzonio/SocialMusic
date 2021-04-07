package it.uninsubria.socialmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uninsubria.socialmusic.R.layout.activity_home

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_home)

        val controllerNavigation = findNavController(R.id.fragmentView)
        val bottomView = findViewById<BottomNavigationView>(R.id.BottomMenu)
        val configurationMenu = AppBarConfiguration(setOf(R.id.homeFragment, R.id.chatFragment, R.id.searchFragment, R.id.profileFragment))

        bottomView.setupWithNavController(controllerNavigation)
        //setupActionBarWithNavController(controllerNavigation, configurationMenu)
    }
}