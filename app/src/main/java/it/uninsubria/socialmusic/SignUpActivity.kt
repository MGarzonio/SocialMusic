package it.uninsubria.socialmusic

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
//import com.google.firebase.auth.ktx.userProfileChangeRequest


class SignUpActivity : AppCompatActivity() {
    private val tag = "SignUpActivity"
    var selectedPhotoUri: Uri? = null
    private var email = ""
    private var psw = ""
    private var name = ""
    private var surname = ""
    private var nick = ""
    private var location = ""
    private val instruments = "1,4,7"
    private val genres = "2,3,5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signUp_button_signUp.setOnClickListener{
            performRegistration()
        }
        loginPage_textView_signUp.setOnClickListener{
            Log.d(tag, "Accessing login page")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        selectPhoto_button_signUp.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
        instrument_button_signUp.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("type", 'I')
            startActivity(intent)
        }
        gen_button_signUp.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("type", 'G')
            startActivity(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            Log.d(tag, "photo was selected")
            val bitmapImage = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profilePhoto_imageView_signUp.setImageBitmap(bitmapImage)
            selectPhoto_button_signUp.alpha = 0F
        }
    }

    private fun sendEmail(){
        val myUser = Firebase.auth.currentUser
        myUser!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "Email sent.")
                    }
                }
    }

    private fun checkNickname(): Boolean{
        nick = nickname_editText_signUp.text.toString()
        var exist = false
        val ref = FirebaseDatabase.getInstance().reference.child("users").orderByChild("username").equalTo(nick)
        //val ref = FirebaseDatabase.getInstance().reference.child("/nicknames").orderByKey().equalTo(nick)
        //val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                /*snapshot.children.forEach {
                    val refUser = it.child("/username")
                    val nickname = refUser.getValue<String>()
                    println(nickname)
                }*/
                println(snapshot.key)
                if(snapshot.childrenCount >= 1) exist = true
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return exist
    }

    private fun performRegistration(){
        email = email_editText_signUp.text.toString()
        psw = password_editText_signUp.text.toString()
        name = name_editText_signUp.text.toString()
        surname = surname_editText_signUp.text.toString()
        nick = nickname_editText_signUp.text.toString()
        location = location_editText_signUp.text.toString()

        if(email.isEmpty() || psw.isEmpty() || name.isEmpty() || surname.isEmpty() || nick.isEmpty() || location.isEmpty()){
            Toast.makeText(this,"Please, fill all the fields with '*'!", Toast.LENGTH_SHORT).show()
            return
        }
        if(checkNickname() == true){
            nickname_editText_signUp.error = "Nickname already in use!"
            Toast.makeText(this,"Nickname already in use!", Toast.LENGTH_SHORT).show()
            return
        }
        //Firebase authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, psw)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                //if successful
                Toast.makeText(this,getString(R.string.creation_success), Toast.LENGTH_SHORT).show()
                Log.d(tag,"Successfully created user whit uid: ${it.result?.user?.uid}")
                if(selectedPhotoUri == null){
                    saveUserToFirebaseDB("default")
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
                Log.d(tag, "Failed to create new user! ${it.message}")
            }
    }

    private fun updateImageToFirebase() {
        val fileName = UUID.randomUUID().toString()
        val fireRef = FirebaseStorage.getInstance().getReference("/images/$fileName")
        fireRef.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(tag, "Successfully update on Firebase Storage image: ${it.metadata?.path}")
                Toast.makeText(this,getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                fireRef.downloadUrl.addOnSuccessListener {url ->
                    Log.d(tag, "File location: $url")
                    saveUserToFirebaseDB(url.toString())
                }
            }
            .addOnFailureListener{
                Log.d(tag, "Updating image failure! ${it.message}")
            }
    }

    private fun saveUserToFirebaseDB(fireImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val fireRef = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val nickRef = FirebaseDatabase.getInstance().getReference("/nicknames/")
        val user = User(uid, nick, fireImageUrl, name, surname, location, instruments, genres)
        nickRef.setValue(nick)
        fireRef.setValue(user)
            .addOnSuccessListener {
                Log.d(tag, "User updated!")
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d(tag, "Updating user failure! ${it.message}")
            }
    }

    /*private fun setNickNImage(){
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = nick
            photoUri = selectedPhotoUri
        }

        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }
    }*/
}