package it.uninsubria.socialmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

    }

    fun doSingIn(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        Toast.makeText(applicationContext, getString(R.string.sing_in_done), Toast.LENGTH_LONG).show()
        startActivity(intent)
    }
}