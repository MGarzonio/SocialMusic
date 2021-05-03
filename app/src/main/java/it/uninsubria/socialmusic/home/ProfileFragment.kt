 package it.uninsubria.socialmusic.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
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
import com.google.android.gms.maps.model.LatLng
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
                             Picasso.get().load(userProfile.profile_image_url).into(profilePhoto)
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

     private fun openMaps(view: View) {
         val intent = Intent(activity, MapsActivity::class.java)
         val position = getPosition(view.context, city.text.toString())
         if (position != null) {
             intent.putExtra("city", city.text.toString())
             intent.putExtra("nickname", nickname.text.toString())
             startActivity(intent)
         } else {
             Toast.makeText(context, R.string.location_error, Toast.LENGTH_LONG).show()
             return
         }
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
         builder.setIcon(android.R.drawable.ic_dialog_alert)
         builder.setTitle("SocialMusic Alert")
         builder.setMessage(getString(R.string.delete_alert_message))
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
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
         val refDBUser = FirebaseDatabase.getInstance().getReference("/users/")
         user!!.delete().addOnSuccessListener {
             Log.d("PROFILE", "user ${userProfile.username} has been cancelled!")
         }
         if (userProfile.profile_image_url != "default") {
             val refStore = FirebaseStorage.getInstance().getReferenceFromUrl(userProfile.profile_image_url)
             refStore.delete()
         }
         refDBUser.child(user.uid).removeValue()
                 .addOnSuccessListener {
                     val intent = Intent(activity, LoginActivity::class.java)
                     startActivity(intent)
                 }
     }

 }
