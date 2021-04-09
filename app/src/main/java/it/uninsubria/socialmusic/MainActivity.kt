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

    private fun isValidPassword(psw: String): Boolean {
        return psw != null && psw.length >= 8
    }
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun doLogin(m: String, p: String):Boolean {
        /*
            TODO login account
        */
        return true
    }

    fun openLogin(view: View) {
        var errorCheck = false
        if(!isValidEmail(mail.text.toString())){
            mail.error = getString(R.string.mail_error)
            errorCheck = true
        }
        if(!isValidPassword(password.text.toString())){
            password.error = getString(R.string.password_error)
            errorCheck = true
        }
        if(doLogin(mail.text.toString(), password.text.toString()) && !errorCheck) {
            val intent = Intent(this,HomeActivity::class.java )
            //intent.putExtra("mail", mail)
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext,getString(R.string.invalid_account), Toast.LENGTH_LONG).show()
        }
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