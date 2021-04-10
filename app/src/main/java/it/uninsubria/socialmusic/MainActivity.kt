package it.uninsubria.socialmusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var mail : EditText
    private lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mail = findViewById<EditText>(R.id.editTextUsername)
        password = findViewById<EditText>(R.id.editTextPassword)
    }

    private fun doLogin(m: String, p: String):Boolean {
        /*
            TODO login account
        */
        return true
    }

    fun openLogin(view: View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    fun openSingIn(view: View) {
        val intent = Intent(this, SingInActivity::class.java)
        startActivity(intent)
    }

    fun openForgot(view: View) {
        val intent = Intent(this, ForgotActivity::class.java)
        startActivity(intent)
    }
}