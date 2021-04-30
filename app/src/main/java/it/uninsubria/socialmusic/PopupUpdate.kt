package it.uninsubria.socialmusic

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_popup_update.*

class PopupUpdate : AppCompatActivity() {
    val oldEmail = Firebase.auth.currentUser!!.email
    private var popupText = ""
    private var popupButton = ""
    private var darkStatusBar = false
    private var type = ""
    private var doneCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_popup_update)
        showPopUp()
    }

    //EMAIL
    private fun updateEmail(old: String) {
        val user = Firebase.auth.currentUser
        val newEmail = email_editText_change.text.toString()
        if (newEmail.isEmpty()) {
            Toast.makeText(this, "Insert email address!", Toast.LENGTH_SHORT).show()
            email_editText_change.error = "Insert email!"
            doneCheck = false
        } else {
            user!!.updateEmail(newEmail)
                .addOnSuccessListener { task ->
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
        val myUser = Firebase.auth.currentUser
        myUser!!.sendEmailVerification()
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
    //PASSWORD
    private fun sendRecoveryMail(to: String) {
        Firebase.auth.sendPasswordResetEmail(to)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.mail_sent), Toast.LENGTH_SHORT).show()
                    Log.d("FORGOT", "Email sent!")
                    logout()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.account_not_found), Toast.LENGTH_LONG).show()
                Log.d("FORGOT", "Failed to send mail! $it")
            }
    }

    private fun openReset(){
        val address = email_editText_change.text.toString()
        if(address.isEmpty()) {
            email_editText_change.error = "Insert email!"
            Toast.makeText(this, "Insert email address!", Toast.LENGTH_SHORT).show()
            doneCheck = false
        } else {
            sendRecoveryMail(address)
        }
    }

    //POPUP
    private fun showPopUp(){
        val bundle = intent.extras
        type = bundle?.getString("type") ?: ""
        popupText = bundle?.getString("popuptext") ?: ""
        popupButton = bundle?.getString("popupbtn") ?: ""
        darkStatusBar = bundle?.getBoolean("darkstatusbar", false) ?: false

        // Set the data
        message_textView_change.text = popupText
        confirm_button_change.text = popupButton

        // Set the Status bar appearance for different API levels
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If you want dark status bar, set darkStatusBar to true
                if (darkStatusBar) {
                    this.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                this.window.statusBarColor = Color.TRANSPARENT
                setWindowFlag(this, false)
            }
        }

        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()


        // Fade animation for the Popup Window
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()


        // Close the Popup Window when you press the button
        confirm_button_change.setOnClickListener{
            when(type){
                "email" ->
                    updateEmail(oldEmail)
                "psw" ->
                    openReset()
            }
            if(doneCheck){
                onBackPressed()
            }

        }
    }

    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }


    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        popup_window_view_with_border.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }
}