package it.uninsubria.socialmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import java.util.regex.Pattern

class SingInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)

    }

    fun doSingIn(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        Toast.makeText(applicationContext, getString(R.string.sing_in_done), Toast.LENGTH_LONG).show()
        startActivity(intent)
    }
}