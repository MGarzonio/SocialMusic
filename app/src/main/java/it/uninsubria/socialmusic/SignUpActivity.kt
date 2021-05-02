package it.uninsubria.socialmusic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_profile.*
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
    private val instruments = "none"
    private val genres = "none"
    private val verified = "no"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signUp_button_signUp.setOnClickListener{
            checkNicknameUnique()
        }
        loginPage_textView_signUp.setOnClickListener{
            Log.d(tag, "Accessing login page")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        selectPhoto_button_signUp.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        deleteImage_button_signUp.alpha = 0F
        deleteImage_button_signUp.setOnClickListener {
            deleteImage()
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
            deleteImage_button_signUp.alpha = 1F
        }
    }

    private fun sendEmail(){
        val myUser = Firebase.auth.currentUser
        myUser!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "Email sent.")
                    } else {
                        Log.d(tag, "Email not sent.")
                    }
                }
    }

    private fun checkNicknameUnique(){
        nick = nickname_editText_signUp.text.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user!!.username == nick) {
                        Log.d("SIGNUP", "This nickname is already taken!")
                        Toast.makeText(applicationContext, getString(R.string.nick_taken), Toast.LENGTH_SHORT).show()
                        nickname_editText_signUp.error = "Already taken"
                        nickname_editText_signUp.requestFocus()
                        return
                    }
                }
                performRegistration()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getPosition(context: Context, city : String): LatLng? {
        val addressList = Geocoder(context, Locale.getDefault()).getFromLocationName(city, 1)
        var latitude : Double = -1.0
        var longitude : Double = -1.0
        if (addressList != null && addressList.size > 0) {
            val address = addressList[0]
            longitude = address.longitude
            latitude = address.latitude
        }
        if(latitude != -1.0 && longitude != -1.0)
            return LatLng(latitude, longitude)
        return null
    }

    private fun performRegistration(){
        email = email_editText_signUp.text.toString()
        psw = password_editText_signUp.text.toString()
        name = name_editText_signUp.text.toString()
        surname = surname_editText_signUp.text.toString()
        location = location_editText_signUp.text.toString()

        if(email.isEmpty() || psw.isEmpty() || name.isEmpty() || surname.isEmpty() || nick.isEmpty() || location.isEmpty()){
            Toast.makeText(this, getString(R.string.fill_all_the_fields), Toast.LENGTH_SHORT).show()
            return
        }
        if(getPosition(this, location) == null){
            Toast.makeText(this, getString(R.string.location_error), Toast.LENGTH_SHORT).show()
            return
        }

        //Firebase authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, psw)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                //if successful
                Toast.makeText(this, getString(R.string.creation_success), Toast.LENGTH_SHORT).show()
                Log.d(tag, "Successfully created user whit uid: ${it.result?.user?.uid}")
                if(selectedPhotoUri == null){
                    saveUserToFirebaseDB("default")
                } else{
                    updateImageToFirebase()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.creation_failure), Toast.LENGTH_SHORT).show()
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
                fireRef.downloadUrl.addOnSuccessListener { url ->
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
        val user = User(uid, nick, fireImageUrl, name, surname, location, instruments, genres, verified)
        fireRef.setValue(user)
            .addOnSuccessListener {
                Log.d(tag, "User updated!")
                sendEmail()
                Toast.makeText(this, getString(R.string.sing_in_done), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d(tag, "Updating user failure! ${it.message}")
            }
    }

    private fun deleteImage(){
        profilePhoto_imageView_signUp.setImageResource(R.mipmap.default_profile)
        selectPhoto_button_signUp.alpha = 1F
        selectedPhotoUri = null
        deleteImage_button_signUp.alpha = 0F
    }
/* PROVA ROTAZIONE IMMAGINI STORTE-------- NON CANCELLARE

    class ImageRotator {
        fun rotateImage(path: String?): Bitmap {
            val bitmap = BitmapFactory.decodeFile(path)
            return rotateImage(bitmap, path)
        }

        fun rotateImage(bitmap: Bitmap, path: String?): Bitmap {
            var rotate = 0
            val exif = ExifInterface(path)
            val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
            val matrix = Matrix()
            matrix.postRotate(rotate.toFloat())
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                    bitmap.height, matrix, true)
        }
    }
    */
}