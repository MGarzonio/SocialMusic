package it.uninsubria.socialmusic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.socialmusic.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener{
            performLogin()
            Log.d(TAG, "Logging in...")
        }
        buttonSignUp.setOnClickListener {
            openSingUp()
        }
        buttonForgot.setOnClickListener {
            openForgot()
        }
    }
    private fun performLogin(){
        val email = email_editText_login.text.toString()
        val psw = password_editText_login.text.toString()
        if(email.isEmpty() || psw.isEmpty()){
            Toast.makeText(this,getString(R.string.empty_user_psw), Toast.LENGTH_SHORT).show()
            return
        }
        //Firebase authentication
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, psw)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this,getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                if(it.message.equals("The email address is badly formatted.")){
                    email_editText_login.error = getString(R.string.invalidEmail)
                }
                if(it.message.equals("The password is invalid or the user does not have a password.")){
                    password_editText_login.error = getString(R.string.invalidPassword)
                }
                Log.d(TAG, "Login failed! ${it.message}")
            }
    }
    private fun openSingUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun openForgot() {
        val intent = Intent(this, ForgotActivity::class.java)
        startActivity(intent)
    }
}