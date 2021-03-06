 package it.uninsubria.socialmusic.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import it.uninsubria.socialmusic.*
import it.uninsubria.socialmusic.R
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
     private lateinit var imageBtnDelete: Button
     private lateinit var userProfile: User
     private var selectedPhotoUri: Uri? = null

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

         val view = inflater.inflate(R.layout.fragment_profile, container, false) as View
         val btnMap = view.findViewById(R.id.mapsButton_Profile) as Button

         nickname = view.findViewById(R.id.nickname_editText_Profile) as EditText
         name = view.findViewById(R.id.name_editText_Profile) as EditText
         surname = view.findViewById(R.id.surname_editText_Profile) as EditText
         city = view.findViewById(R.id.location_editText_Profile) as EditText
         btnMail = view.findViewById(R.id.email_button_Profile) as Button
         btnPassword = view.findViewById(R.id.password_button_Profile) as Button
         btnEditProfile = view.findViewById(R.id.buttonEdit_Profile) as Button
         btnPhoto = view.findViewById(R.id.selectPhoto_button_Profile) as Button
         profilePhoto = view.findViewById(R.id.profilePhoto_imageView_Profile) as de.hdodenhof.circleimageview.CircleImageView
         btnGenres = view.findViewById(R.id.gen_button_Profile) as Button
         btnInstruments = view.findViewById(R.id.instrument_button_Profile) as Button
         btnLogout = view.findViewById(R.id.buttonLogout_Profile) as Button
         imageBtnDelete = view.findViewById(R.id.deleteImage_button_profile) as Button

         btnEditProfile.setOnClickListener(this)
         btnLogout.setOnClickListener(this)
         btnMap.setOnClickListener(this)
         btnMail.setOnClickListener(this)
         btnPassword.setOnClickListener(this)
         btnGenres.setOnClickListener(this)
         btnInstruments.setOnClickListener(this)
         btnPhoto.setOnClickListener(this)
         imageBtnDelete.setOnClickListener(this)

         loadProfileFromFirebase(true)

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
             R.id.email_button_Profile -> editEmail(view)
             R.id.password_button_Profile -> editPassword(view)
             R.id.deleteImage_button_profile -> deletePhoto()
         }
     }

     private fun loadProfileFromFirebase(setNick: Boolean){
         switchEditableProfile(false)
         val myUser = Firebase.auth.currentUser
         val userID = myUser!!.uid
         val ref = FirebaseDatabase.getInstance().getReference("/users/$userID")
         ref.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 if(snapshot.exists()) {
                     userProfile = snapshot.getValue(User::class.java)!!
                     if(setNick){
                         nickname.setText(userProfile.username)
                         if (userProfile.profile_image_url != "default") {
                             Glide.with(context!!).load(userProfile.profile_image_url).into(profilePhoto)
                             btnPhoto.alpha = 0F
                         }
                     }
                     name.setText(userProfile.name)
                     surname.setText(userProfile.surname)
                     city.setText(userProfile.location)
                 }
             }

             override fun onCancelled(error: DatabaseError) {
                 Toast.makeText(context, "This user has been deleted!", Toast.LENGTH_SHORT).show()
                 Log.d("PROFILE", error.toString())
             }
         })
     }

     private fun editProfile() {
         when(btnEditProfile.text.toString()) {
             getString(R.string.save) -> checkImageChanged()
             getString(R.string.edit_profile) -> switchEditableProfile(true)
         }
     }

     private fun logoutBtnAction(){
         when(btnLogout.text.toString()){
             getString(R.string.logout) -> doLogout()
             getString(R.string.delete) -> openPopup()
         }
     }

     private fun switchEditableProfile(modifiable: Boolean) {
         nickname.isEnabled = modifiable
         name.isEnabled = modifiable
         surname.isEnabled = modifiable
         city.isEnabled = modifiable
         btnPhoto.isClickable = modifiable
         imageBtnDelete.isClickable = modifiable
         when(modifiable){
             true -> {
                 if (userProfile.profile_image_url != "default"){
                     imageBtnDelete.alpha = 1F
                 }
                 btnLogout.text = getString(R.string.delete)
                 btnLogout.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_delete_forever_24, 0, 0);
                 btnEditProfile.text = getString(R.string.save)
                 btnEditProfile.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_save_alt_24, 0, 0);
             }
             false -> {
                 imageBtnDelete.alpha = 0F
                 btnLogout.text = getString(R.string.logout)
                 btnLogout.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_logout_24, 0, 0);
                 btnEditProfile.text = getString(R.string.edit_profile)
                 btnEditProfile.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_edit_24, 0, 0);
             }
         }
     }

     private fun checkImageChanged() {
         if(selectedPhotoUri.toString() == "deleted"){
             updateProfileToFirebase("default")
         }else if(selectedPhotoUri == null) {
             updateProfileToFirebase(userProfile.profile_image_url)
         } else {
             setImageToFirebase()
         }
     }

     private fun updateProfileToFirebase(photoUrl: String){
         switchEditableProfile(false)
         loadProfileFromFirebase(false)
         val nic = nickname.text.toString()
         val nam = name.text.toString()
         val sur = surname.text.toString()
         val loc = city.text.toString()
         if(userProfile!!.profile_image_url != photoUrl){
             setDataToFirebase("profile_image_url", photoUrl)
         }
         if(userProfile!!.username != nic){
             checkNicknameUnique(nic)
         }
         if(userProfile!!.name != nam){
             setDataToFirebase("name", nam)
         }
         if(userProfile!!.surname != sur){
             setDataToFirebase("surname", sur)
         }
         if(userProfile!!.location != loc){
             setDataToFirebase("location", loc)
         }
     }

     private fun checkNicknameUnique(nick: String){
         val ref = FirebaseDatabase.getInstance().getReference("/users")
         ref.addListenerForSingleValueEvent(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 snapshot.children.forEach {
                     val user = it.getValue(User::class.java)
                     if (user!!.username == nick) {
                         Log.d("SIGNUP", "This nickname is already taken!")
                         Toast.makeText(context, getString(R.string.nick_taken), Toast.LENGTH_SHORT).show()
                         nickname_editText_Profile.error = "Already taken"
                         nickname_editText_Profile.requestFocus()
                         switchEditableProfile(true)
                         return
                     }
                 }
                 setDataToFirebase("username", nick)
             }
             override fun onCancelled(error: DatabaseError) {}
         })
     }

     private fun setDataToFirebase(child: String, value: String){
         val uid = FirebaseAuth.getInstance().currentUser!!.uid
         val ref = FirebaseDatabase.getInstance().getReference("users/$uid/$child")
         ref.setValue(value)
                 .addOnSuccessListener {
                     Log.d(tag, "$child updated!")
                     Toast.makeText(context,"$child Update", Toast.LENGTH_SHORT).show()
                 }
                 .addOnFailureListener{
                     Log.d(tag, "Updating $child failure! ${it.message}")
                     Toast.makeText(context,"Updating $child failure!", Toast.LENGTH_SHORT).show()
                 }
     }

     private fun setImageToFirebase(){
         val fileName = UUID.randomUUID().toString()
         val fireRef = FirebaseStorage.getInstance().getReference("/images/$fileName")
         fireRef.putFile(selectedPhotoUri!!)
             .addOnSuccessListener {
                 Log.d(tag, "Successfully update on Firebase Storage image: ${it.metadata?.path}")
                 Toast.makeText(context,getString(R.string.update_success), Toast.LENGTH_SHORT).show()
                 fireRef.downloadUrl
                         .addOnSuccessListener {url ->
                             updateProfileToFirebase(url.toString())
                             Log.d(tag, "File location: $url")
                 }
             }
             .addOnFailureListener{
                 Toast.makeText(context,"Unable to load this profile photo!", Toast.LENGTH_SHORT).show()
                 Log.d(tag, "Updating image failure! ${it.message}")
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

     private fun editEmail(view: View) {
         val intent = Intent(view.context, PopupActivity::class.java)
         intent.putExtra("type", "email")
         intent.putExtra("popuptext", getString(R.string.change_email_message))
         intent.putExtra("popupbtn", getString(R.string.send))
         intent.putExtra("darkstatusbar", false)
         startActivity(intent)
     }

     private fun editPassword(view: View){
         val intent = Intent(view.context, PopupActivity::class.java)
         intent.putExtra("type", "psw")
         intent.putExtra("popuptext", getString(R.string.forgot_psw_message))
         intent.putExtra("popupbtn", getString(R.string.submit))
         intent.putExtra("darkstatusbar", false)
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
             deleteImage_button_profile.alpha = 1f
         }
     }

     private fun deletePhoto() {
         profilePhoto.setImageResource(R.mipmap.default_profile)
         selectPhoto_button_Profile.alpha = 1F
         selectedPhotoUri = "deleted".toUri()
         if (userProfile.profile_image_url != "default") {
             val refStore = FirebaseStorage.getInstance().getReferenceFromUrl(userProfile.profile_image_url)
             refStore.delete()
         }
         deleteImage_button_profile.alpha = 0F
     }

     private fun openPopup(){
         val builder = AlertDialog.Builder(context)
         builder.setIcon(R.drawable.ic_round_warning_24)
         builder.setTitle("SocialMusic Alert")
         builder.setMessage(getString(R.string.delete_alert_message))
         builder.setPositiveButton(android.R.string.yes) { dialog, which ->
             deleteUser()
         }
         builder.setNegativeButton(android.R.string.no) { dialog, which ->
             Toast.makeText(context, android.R.string.no, Toast.LENGTH_SHORT).show()
         }
         builder.show()
     }

     private fun doLogout() {
         FirebaseAuth.getInstance().signOut()
         val intent = Intent(context, LoginActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
         startActivity(intent)
     }

     private fun deleteUser() {
         val user = FirebaseAuth.getInstance().currentUser
         //USER FROM AUTHENTICATION
         user!!.delete()
             .addOnSuccessListener {
                 Log.d("PROFILE", "user ${userProfile.username} has been cancelled!")
                 removeAllUserData(user)
         }
             .addOnFailureListener {
                 Toast.makeText(context,"User deletion failed!", Toast.LENGTH_SHORT).show()
                 return@addOnFailureListener
             }

     }
     private fun removeAllUserData(user: FirebaseUser){
         val refDBUser = FirebaseDatabase.getInstance().getReference("/users/")
         val refDBLatest = FirebaseDatabase.getInstance().getReference("/latest-message/")
         val refDBMess = FirebaseDatabase.getInstance().getReference("/user-messages/")
         val refDBPost = FirebaseDatabase.getInstance().getReference("/posts/")
         val refLike = FirebaseDatabase.getInstance().getReference("/postlike/")
         val refDislike = FirebaseDatabase.getInstance().getReference("/postdislike/")

         //IMAGE FROM STORE
         if (userProfile.profile_image_url != "default") {
             val refStore = FirebaseStorage.getInstance().getReferenceFromUrl(userProfile.profile_image_url)
             refStore.delete()
         }
         //MESSAGES FROM DATABASE LATEST-MESSAGE
         removeMessages(refDBLatest)
         //MESSAGES FROM DATABASE USER-MESSAGES
         removeMessages(refDBMess)
         //POSTS FROM DATABASE
         removePosts(refDBPost)
         //LIKE FROM DATABASE
         removeLike(refLike)
         removeLike(refDislike)
         //USER FROM DATABASE
         refDBUser.child(user.uid).removeValue()
             .addOnSuccessListener {
                 val intent = Intent(activity, LoginActivity::class.java)
                 startActivity(intent)
             }
     }

     private fun removePosts(ref: DatabaseReference){
         ref.addListenerForSingleValueEvent(object: ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 snapshot.children.forEach {
                     val postValue = it.child("fromID").value
                     if(postValue == userProfile.uid){
                         ref.child("${it.key}").removeValue()
                     }
                 }
             }
             override fun onCancelled(error: DatabaseError) {}
         })
     }
     private fun removeMessages(ref: DatabaseReference){
         ref.child(userProfile.uid).removeValue()
             .addOnSuccessListener {
                 Log.d("PROFILE", "user ${userProfile.username}'s messages have been cancelled!")
             }
         ref.addListenerForSingleValueEvent(object: ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 snapshot.children.forEach {
                     if(it.child(userProfile.uid).exists()){
                         ref.child("${it.key}/${userProfile.uid}").removeValue()
                     }
                 }
             }
             override fun onCancelled(error: DatabaseError) {}
         })
     }
     private fun removeLike(ref: DatabaseReference){
         ref.addListenerForSingleValueEvent(object: ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 snapshot.children.forEach { itFirst ->
                     itFirst.ref.addListenerForSingleValueEvent(object: ValueEventListener{
                         override fun onDataChange(snapshot: DataSnapshot) {
                             snapshot.children.forEach {
                                 if(it.value == userProfile.uid){
                                     ref.child("${itFirst.key}/${it.key}").removeValue()
                                 }
                             }
                         }
                         override fun onCancelled(error: DatabaseError) {}
                     })
                 }
             }
             override fun onCancelled(error: DatabaseError) {}
         })
     }
 }

