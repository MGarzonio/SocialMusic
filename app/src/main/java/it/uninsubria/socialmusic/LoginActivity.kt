package it.uninsubria.socialmusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import it.uninsubria.socialmusic.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var mail : EditText
    private lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun openForgot(view: View) {
        val intent = Intent(this, ForgotActivity::class.java)
        startActivity(intent)
    }
}