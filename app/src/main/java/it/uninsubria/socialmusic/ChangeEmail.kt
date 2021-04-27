package it.uninsubria.socialmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_change_email.*

class ChangeEmail : AppCompatActivity() {
    private lateinit var oldEmail: String
    private lateinit  var myUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        myUser = Firebase.auth.currentUser
        oldEmail = myUser.email
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
        confirm_button_change.setOnClickListener{
            updateEmail(oldEmail)
        }
    }

    private fun updateEmail(old: String) {
        val user = Firebase.auth.currentUser
        val newEmail = email_editText_change.text.toString()
        if (newEmail.isEmpty()) {
            Toast.makeText(this, "Insert email address!", Toast.LENGTH_SHORT).show()
        } else {
            user!!.updateEmail(newEmail)
                .addOnSuccessListener {
                    sendEmail(old)
                    Log.d("EMAIL", "User email address updated.")
                }
                .addOnFailureListener {
                    email_editText_change.error = getString(R.string.invalidEmail)
                    Log.d("EMAIL", "Failed $it")
                }
        }
    }

    private fun sendEmail(old: String) {
        myUser.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logout()
                    Log.d("EMAIL", "Email sent.")
                } else {
                    email_editText_change.error = getString(R.string.invalidEmail)
                    myUser.updateEmail(old)
                    Log.d("EMAIL", "Failed")
                }
            }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}