package it.uninsubria.socialmusic

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)
    }

    fun doLogin(m:String, p:String):Boolean
    {
        /*
            LOGIN ACCOUNT FIREBASE
        */
        return true;
    }

    fun openSingIn(view: View) {
        var mail = editTextUsername.text.toString()
        var password = editTextPassword.text.toString()
        if(doLogin(mail, password)) {
            val intent: Intent = Intent(this, SingInActivity::class.java)
            intent.putExtra("mail", mail)
            startActivity(intent)
        }
    }
}