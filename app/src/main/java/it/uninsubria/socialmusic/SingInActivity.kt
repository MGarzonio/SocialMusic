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

    private lateinit var mail : EditText
    private lateinit var password : EditText
    private lateinit var nick : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)

        mail = findViewById<EditText>(R.id.editTextEmail)
        password = findViewById<EditText>(R.id.editTextPassword)
        nick = findViewById<EditText>(R.id.editTextNickname)

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

    private fun singIn(){
        /*
            TODO() sing in
         */
    }

    fun doSingIn(view: View) {
        var errorCheck = false
        if(!isValidEmail(mail.text.toString())){
            mail.error = getString(R.string.mail_error)
            errorCheck = true
        }
        if(!isValidPassword(password.text.toString())){
            password.error = getString(R.string.password_error)
            errorCheck = true
        }
        if(nick.text.toString().isEmpty()){
            nick.error = getString(R.string.empty_string)
        }
        if(!errorCheck){
            singIn()
            val intent = Intent(this, MainActivity::class.java)
            Toast.makeText(applicationContext, getString(R.string.sing_in_done), Toast.LENGTH_LONG).show()
            startActivity(intent)
        }
    }
}