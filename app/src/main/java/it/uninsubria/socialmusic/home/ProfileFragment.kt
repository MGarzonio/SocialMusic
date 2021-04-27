 package it.uninsubria.socialmusic.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import it.uninsubria.socialmusic.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*

 class ProfileFragment : Fragment(), View.OnClickListener {

     private lateinit var nickname: EditText
     private lateinit var name: EditText
     private lateinit var surname: EditText
     private lateinit var city: EditText
     private lateinit var btnMail: Button
     private lateinit var btnPassword: Button
     private lateinit var btnEditProfile: Button
     private lateinit var btnPhoto : Button
     private lateinit var profilePhoto : de.hdodenhof.circleimageview.CircleImageView
     private  lateinit var btnGenres: Button
     private  lateinit var btnInstruments: Button
     private lateinit var btnLogout: Button
     private var userProfile: User? = null
     private var selectedPhotoUri: Uri? = null
     private var photoUrl: String = "default"

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

         val view = inflater.inflate(R.layout.fragment_profile, container, false) as View
         val btnMap = view.findViewById(R.id.mapsButton_Profile) as ImageView

         nickname = view.findViewById(R.id.nickname_editText_Profile) as EditText
         name = view.findViewById(R.id.name_editText_Profile) as EditText
         surname = view.findViewById(R.id.surname_editText_Profile) as EditText
         city = view.findViewById(R.id.location_editText_Profile) as EditText
         btnMail = view.findViewById(R.id.email_button_Profile) as Button
         btnPassword = view.findViewById(R.id.password_button_profile) as Button
         btnEditProfile = view.findViewById(R.id.buttonEdit_Profile) as Button
         btnPhoto = view.findViewById(R.id.selectPhoto_button_Profile) as Button
         profilePhoto = view.findViewById(R.id.profilePhoto_imageView_Profile) as de.hdodenhof.circleimageview.CircleImageView
         btnGenres = view.findViewById(R.id.gen_button_Profile) as Button
         btnInstruments = view.findViewById(R.id.instrument_button_Profile) as Button
         btnLogout = view.findViewById(R.id.buttonLogout_Profile) as Button

         btnEditProfile.setOnClickListener(this)
         btnLogout.setOnClickListener(this)
         btnMap.setOnClickListener(this)
         btnMail.setOnClickListener(this)
         btnPassword.setOnClickListener(this)
         btnGenres.setOnClickListener(this)
         btnInstruments.setOnClickListener(this)
         btnPhoto.setOnClickListener(this)

         loadProfileFromFirebase()

         return view
     }

     override fun onClick(view: View) {
         when (view.id) {
             R.id.buttonEdit_Profile -> editProfile()
             R.id.buttonLogout_Profile -> logoutBtnAction()
             R.id.mapsButton_Profile -> openMaps(view)
             R.id.instrument_button_Profile -> openInstruments(view)
             R.id.gen_button_Profile -> openGenres(view)
             R.id.selectPhoto_button_Profile -> loadImageFromGallery(view)
             R.id.email_button_Profile -> editEmail()
             R.id.password_button_profile -> editPassword()
         }
     }

     private fun loadProfileFromFirebase(){
         switchEditableProfile(false)
         val myUser = Firebase.auth.currentUser
         val userID = myUser.uid
         val ref = FirebaseDatabase.getInstance().getReference("/users/$userID")
         ref.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.exists()) {
                     userProfile = snapshot.getValue(User::class.java)!!
                     nickname.setText(userProfile?.username)
                     name.setText(userProfile?.name)
                     surname.setText(userProfile?.surname)
                     city.setText(userProfile?.location)
                     if (userProfile?.profile_image_url != "default") {
                         Picasso.get().load(userProfile?.profile_image_url).into(profilePhoto)
                         btnPhoto.alpha = 0F
                     }
                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 Toast.makeText(context, "This user has been deleted!", Toast.LENGTH_SHORT)
                 Log.d("PROFILE", error.toString())
             }
         })
     }

     private fun editProfile() {
         when(btnEditProfile.text.toString()) {
             getString(R.string.save) -> saveProfileOnFirebase()
             getString(R.string.edit_profile) -> switchEditableProfile(true)
         }
     }

     private fun saveProfileOnFirebase() {
         switchEditableProfile(false)
         val nic = nickname.text.toString()
         val nam = name.text.toString()
         val sur = surname.text.toString()
         val loc = city.text.toString()
         val ins = "none"
         val gen = "none"
         val uid = FirebaseAuth.getInstance().currentUser.uid
         if(selectedPhotoUri != null){
             updateImageToFirebase()
         }
         val userClass = User(uid, nic, photoUrl, nam, sur, loc, ins, gen)
         val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
         ref.setValue(userClass)
             .addOnSuccessListener {
                 Log.d(tag, "User updated!")
                 Toast.makeText(context,"User Update", Toast.LENGTH_SHORT)
             }
             .addOnFailureListener{
                 Log.d(tag, "Updating user failure! ${it.message}")
             }
     }

     private fun updateImageToFirebase(){
         val fileName = UUID.randomUUID().toString()
         val fireRef = FirebaseStorage.getInstance().getReference("/images/$fileName")
         fireRef.putFile(selectedPhotoUri!!)
             .addOnSuccessListener {
                 Log.d(tag, "Successfully update on Firebase Storage image: ${it.metadata?.path}")
                 Toast.makeText(context,getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                 fireRef.downloadUrl.addOnSuccessListener {url ->
                     photoUrl = url.toString()
                     Log.d(tag, "File location: $url")
                 }
             }
             .addOnFailureListener{
                 Log.d(tag, "Updating image failure! ${it.message}")
             }
     }

     private fun switchEditableProfile(modifiable: Boolean) {
         nickname.isEnabled = modifiable
         name.isEnabled = modifiable
         surname.isEnabled = modifiable
         city.isEnabled = modifiable
         btnPhoto.isClickable = modifiable
         btnInstruments.isClickable = modifiable
         btnGenres.isClickable = modifiable
         when(modifiable){
             true -> {
                 btnLogout.text = "delete"
                 btnEditProfile.text = getString(R.string.save)
             }
             false -> {
                 btnLogout.text = "logout"
                 btnEditProfile.text = getString(R.string.edit_profile)
             }
         }
     }

     private fun editEmail() {
         val intent = Intent(activity, ChangeEmail::class.java)
         startActivity(intent)
     }

     private fun editPassword(){
         val intent = Intent(activity, ResetPswActivity::class.java)
         startActivity(intent)
     }

     private fun openGenres(view: View) {
         val intent = Intent(activity, ListActivity::class.java)
         intent.putExtra("type", 'G')
         startActivity(intent)
     }

     private fun openInstruments(view: View) {
         val intent = Intent(activity, ListActivity::class.java)
         intent.putExtra("type", 'I')
         startActivity(intent)
     }

     private fun openMaps(view: View) {
         val intent = Intent(activity, MapsActivity::class.java)
         intent.putExtra("city", city.text.toString())
         intent.putExtra("nickname", nickname.text.toString())
         startActivity(intent)
     }

     private fun loadImageFromGallery(view: View){
         val intent = Intent(Intent.ACTION_PICK)
         intent.type = "image/*"
         startActivityForResult(intent,0)
     }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
             selectedPhotoUri = data.data
             val bitmapImage = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedPhotoUri)
             profilePhoto_imageView_Profile.setImageBitmap(bitmapImage)
             selectPhoto_button_Profile.alpha = 0f
         }
     }

     private fun logoutBtnAction(){
         when(btnLogout.text.toString()){
             "logout" -> doLogout()
             "delete" -> deleteUser()
         }
     }

     private fun doLogout() {
         FirebaseAuth.getInstance().signOut()
         val intent = Intent(context, LoginActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
         startActivity(intent)
     }

     private fun deleteUser(){
         if(userProfile != null) {
             val user = FirebaseAuth.getInstance().currentUser!!
             val refDBUser = FirebaseDatabase.getInstance().getReference("/users/")
             user.delete().addOnSuccessListener {
                 Log.d("PROFILE", "user ${userProfile!!.username} has been cancelled!")
             }
             if(userProfile!!.profile_image_url != "default") {
                 val refStore = FirebaseStorage.getInstance().getReferenceFromUrl(userProfile!!.profile_image_url)
                 refStore.delete()
             }
             refDBUser.child(user.uid).removeValue()
                 .addOnSuccessListener {
                     val intent = Intent(activity, LoginActivity::class.java)
                     startActivity(intent)
                 }
         }
     }

 }
