package it.uninsubria.socialmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class ForgotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
    }

    private fun isValidPassword(psw: String): Boolean {
        return psw.length >= 4
    }
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun sendRecoveryMail(to: String) :Boolean {
        Firebase.auth.sendPasswordResetEmail(to)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FORGOT", "Email sent.")
                    }
                }
        return true
    }

    fun openMain(view: View) {
        val mail = findViewById<EditText>(R.id.editTextEmail)
        val address = mail.text.toString()
        var checkError = false
        if (!isValidEmail(address)) {
            mail.error = getString(R.string.mail_error)
            checkError = true
        }
        if (!checkError) if (sendRecoveryMail(address)) {
            Toast.makeText(applicationContext, getString(R.string.mail_sent), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, getString(R.string.account_not_found), Toast.LENGTH_LONG).show()
        }
    }
}