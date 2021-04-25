 package it.uninsubria.socialmusic.home

import android.app.Activity
import android.content.Intent
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
import com.squareup.picasso.Picasso
import it.uninsubria.socialmusic.*
import kotlinx.android.synthetic.main.fragment_profile.*

 class ProfileFragment : Fragment(), View.OnClickListener {

     private lateinit var nickname: EditText
     private lateinit var name: EditText
     private lateinit var surname: EditText
     private lateinit var city: EditText
     private lateinit var mail: EditText
     private lateinit var btnMail: Button
     private lateinit var btnPassword: Button
     private lateinit var btnEditProfile: Button
     private lateinit var btnPhoto : Button
     private lateinit var profilePhoto : de.hdodenhof.circleimageview.CircleImageView
     private  lateinit var btnGenres: Button
     private  lateinit var btnInstruments: Button

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

         val view = inflater.inflate(R.layout.fragment_profile, container, false) as View
         val btnMap = view.findViewById(R.id.mapsButton_Profile) as ImageView
         val btnLogout = view.findViewById(R.id.buttonLogout_Profile) as Button

         nickname = view.findViewById(R.id.nickname_editText_Profile) as EditText
         name = view.findViewById(R.id.name_editText_Profile) as EditText
         surname = view.findViewById(R.id.surname_editText_Profile) as EditText
         city = view.findViewById(R.id.location_editText_Profile) as EditText
         mail =view.findViewById(R.id.email_editText_profile) as EditText
         btnMail = view.findViewById(R.id.email_button_Profile) as Button
         btnPassword = view.findViewById(R.id.password_button_profile) as Button
         btnEditProfile = view.findViewById(R.id.buttonEdit_Profile) as Button
         btnPhoto = view.findViewById(R.id.selectPhoto_button_Profile) as Button
         profilePhoto = view.findViewById(R.id.profilePhoto_imageView_Profile) as de.hdodenhof.circleimageview.CircleImageView
         btnGenres = view.findViewById(R.id.gen_button_Profile) as Button
         btnInstruments = view.findViewById(R.id.instrument_button_Profile) as Button

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
             R.id.buttonLogout_Profile -> doLogout()
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
         setEmailEditable(false)
         val myUser = Firebase.auth.currentUser
         val userID = myUser.uid
         val ref = FirebaseDatabase.getInstance().getReference("/users/$userID")
         ref.addListenerForSingleValueEvent(object: ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 val user = snapshot.getValue(User::class.java)
                 nickname.setText(user?.username)
                 name.setText(user?.name)
                 surname.setText(user?.surname)
                 city.setText(user?.location)
                 mail.setText(myUser.email)
                 if(user?.profile_image_url != "default") {
                     Picasso.get().load(user?.profile_image_url).into(profilePhoto)
                     btnPhoto.alpha = 0F
                 }
             }
             override fun onCancelled(error: DatabaseError) {
             }
         })
     }

     private fun editProfile() {
         when(btnEditProfile.text.toString()) {
             getString(R.string.save) -> saveProfile()
             getString(R.string.edit_profile) -> switchEditableProfile(true)
         }
     }

     private fun saveProfile() {
         switchEditableProfile(false)

         //TODO(load new values on firebase)
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
                 btnEditProfile.text = getString(R.string.save)
             }
             false -> {
                 btnEditProfile.text = getString(R.string.edit_profile)
             }
         }
     }

     private fun editEmail(){
         val oldEmail = mail.text.toString()
         when(btnMail.text.toString()) {
             getString(R.string.save) -> updateEmail(oldEmail)
             getString(R.string.edit) -> setEmailEditable(true)
         }
     }

     private fun setEmailEditable(editable: Boolean){
         mail.isEnabled = editable
         when(editable){
             true -> {
                 btnMail.text = getString(R.string.save)
                 mail.text.clear()
             }
             false -> {
                 btnMail.text = getString(R.string.edit)
             }
         }
     }

     private fun updateEmail(old: String){
         val user = Firebase.auth.currentUser
         val newEmail = mail.text.toString()
         if(newEmail.isEmpty()) {
             Toast.makeText(context, "Insert email address!", Toast.LENGTH_SHORT).show()
         } else{
             user!!.updateEmail(newEmail)
                     .addOnSuccessListener{ task ->
                             sendEmail(old)
                             Log.d("EMAIL", "User email address updated.")
                     }
                     .addOnFailureListener {
                         mail.error = getString(R.string.invalidEmail)
                         Log.d("EMAIL", "Failed ${it.toString()}")
                     }
         }
     }

     private fun editPassword(){
         val intent = Intent(activity, ForgotActivity::class.java)
         startActivity(intent)
     }

     private fun sendEmail(old: String){
         val myUser = Firebase.auth.currentUser
         myUser!!.sendEmailVerification()
                 .addOnCompleteListener { task ->
                     if (task.isSuccessful) {
                         doLogout()
                         Log.d("EMAIL", "Email sent.")
                     } else{
                         mail.error = getString(R.string.invalidEmail)
                         mail.setText(old)
                         myUser!!.updateEmail(old)
                         Log.d("EMAIL", "Failed")
                     }
                 }
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

     private fun doLogout() {
         FirebaseAuth.getInstance().signOut()
         val intent = Intent(context, LoginActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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
             val selectedPhotoUri = data.data
             val bitmapImage = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedPhotoUri)
             profilePhoto_imageView_Profile.setImageBitmap(bitmapImage)
             selectPhoto_button_Profile.alpha = 0f
         }
     }

 }