package it.uninsubria.socialmusic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import it.uninsubria.socialmusic.home.HomeActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {
    val TAG = "SignUpActivity"
    var selectedPhotoUri: Uri? = null
    val defaultID = "6N9HD0c5WgPsakocjfluSiSI0hm2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signUp_button_signUp.setOnClickListener{
            performRegistration()
        }
        loginPage_textView_signUp.setOnClickListener{
            Log.d(TAG, "Accessing login page")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        selectPhoto_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            Log.d(TAG, "photo was selected")
            val bitmapImage = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profilePhoto_imageView_signUp.setImageBitmap(bitmapImage)
            selectPhoto_button.alpha = 0f
        }
    }

    private fun sendEmail(){
        val myUser = Firebase.auth.currentUser
        myUser!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                    }
                }
    }
    private fun performRegistration(){
        val email = email_editText_signUp.text.toString()
        val psw = password_editText_signUp.text.toString()

        if(email.isEmpty() || psw.isEmpty()){
            Toast.makeText(this,getString(R.string.empty_user_psw), Toast.LENGTH_SHORT).show()
            return
        }
        //Firebase authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, psw)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                //if successful
                Toast.makeText(this,getString(R.string.creation_success), Toast.LENGTH_SHORT).show()
                Log.d(TAG,"Successfully created user whit uid: ${it.result?.user?.uid}")
                if(selectedPhotoUri == null){
                    val ref = FirebaseDatabase.getInstance().getReference("/users/$defaultID")
                    ref.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val imageDefault = snapshot.getValue(User::class.java)
                            saveUserToFirebaseDB(imageDefault!!.profile_image_url)
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    } )
                } else{
                    updateImageToFirebase()
                }
                sendEmail()
            }
            .addOnFailureListener {
                Toast.makeText(this,getString(R.string.creation_failure), Toast.LENGTH_SHORT).show()
                if(it.message.equals("The email address is badly formatted.")){
                    email_editText_signUp.error = getString(R.string.invalidEmail)
                }
                if(it.message.equals("The given password is invalid. [ Password should be at least 6 characters ]")){
                    password_editText_signUp.error = getString(R.string.invalidPassword)
                }
                Log.d(TAG, "Failed to create new user! ${it.message}")
            }
    }
    private fun updateImageToFirebase() {
        val fileName = UUID.randomUUID().toString()
        val fireRef = FirebaseStorage.getInstance().getReference("/images/$fileName")
        fireRef.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully update on Firebase Storage image: ${it.metadata?.path}")
                Toast.makeText(this,getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                fireRef.downloadUrl.addOnSuccessListener {url ->
                    Log.d(TAG, "File location: $url")
                    saveUserToFirebaseDB(url.toString())
                }
            }
            .addOnFailureListener{
                Log.d(TAG, "Updating image failure! ${it.message}")
            }
    }
    private fun saveUserToFirebaseDB(fireImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val fireRef = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_editText_signUp.text.toString(),fireImageUrl)
        fireRef.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "User updated!")
                showPopup()
            }
            .addOnFailureListener{
                Log.d(TAG, "Updating user failure! ${it.message}")
            }
    }
    private fun showPopup(){
        val popup = PopupWindow(this)
        val view = layoutInflater.inflate(R.layout.activity_sign_up, null)
        popup.contentView = view
        val popUpView = view.findViewById<TextView>(R.id.signUp_popUp_textView)
        popUpView.setOnClickListener {
            if(Firebase.auth.currentUser.isEmailVerified){
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else{
                Toast.makeText(this,getString(R.string.verify_mail_address), Toast.LENGTH_SHORT).show()
            }
        }
        popup.showAsDropDown(signUp_button_signUp)
    }
}