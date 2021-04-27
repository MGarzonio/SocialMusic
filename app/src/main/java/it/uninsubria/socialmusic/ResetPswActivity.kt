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

class ResetPswActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_psw)
    }

    private fun sendRecoveryMail(to: String) {
        Firebase.auth.sendPasswordResetEmail(to)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, getString(R.string.mail_sent), Toast.LENGTH_SHORT).show()
                        Log.d("FORGOT", "Email sent!")
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, getString(R.string.account_not_found), Toast.LENGTH_LONG).show()
                    Log.d("FORGOT", "Failed to send mail! $it")
                }
    }

    fun openMain(view: View) {
        val mail = findViewById<EditText>(R.id.editTextEmail)
        val address = mail.text.toString()
        if(address.isEmpty()) {
            mail.error = "Insert email!"
        } else {
            sendRecoveryMail(address)
        }
    }

}