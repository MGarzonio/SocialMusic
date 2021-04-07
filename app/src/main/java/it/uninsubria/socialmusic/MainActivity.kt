package it.uninsubria.socialmusic

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun doLogin(m: String, p: String):Boolean {
        /*
            LOGIN ACCOUNT FIREBASE
        */
        return true
    }

    fun openLogin(view: View) {
        val mail = editTextUsername.text.toString()
        val password = editTextPassword.text.toString()
        //if(doLogin(mail, password)) {
            val intent = Intent(this,HomeActivity::class.java )
            //intent.putExtra("mail", mail)
            startActivity(intent)
        //}
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