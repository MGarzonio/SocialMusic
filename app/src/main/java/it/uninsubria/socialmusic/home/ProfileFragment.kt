 package it.uninsubria.socialmusic.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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
     private lateinit var password: EditText
     private lateinit var btnEditProfile: Button
     private lateinit var btnPhoto : Button
     private lateinit var profilePhoto : de.hdodenhof.circleimageview.CircleImageView

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

         val view = inflater.inflate(R.layout.fragment_profile, container, false) as View
         val btnMap = view.findViewById(R.id.mapsButton_Profile) as ImageView
         val btnLogout = view.findViewById(R.id.buttonLogout_Profile) as Button
         val btnGenres = view.findViewById(R.id.gen_button_Profile) as Button
         val btnInstruments = view.findViewById(R.id.instrument_button_Profile) as Button

         nickname = view.findViewById(R.id.nickname_editText_Profile) as EditText
         name = view.findViewById(R.id.name_editText_Profile) as EditText
         surname = view.findViewById(R.id.surname_editText_Profile) as EditText
         city = view.findViewById(R.id.location_editText_Profile) as EditText
         mail = view.findViewById(R.id.email_editText_Profile) as EditText
         password = view.findViewById(R.id.password_editText_Profile) as EditText
         btnEditProfile = view.findViewById(R.id.buttonEdit_Profile) as Button
         btnPhoto = view.findViewById(R.id.selectPhoto_button_Profile) as Button
         profilePhoto = view.findViewById(R.id.profilePhoto_imageView_Profile) as de.hdodenhof.circleimageview.CircleImageView

         btnEditProfile.setOnClickListener(this)
         btnLogout.setOnClickListener(this)
         btnMap.setOnClickListener(this)
         btnGenres.setOnClickListener(this)
         btnInstruments.setOnClickListener(this)
         btnPhoto.setOnClickListener(this)

         loadProfileFromFirebase()

         return view
     }

     override fun onClick(view: View) {
         when (view.id) {
             R.id.buttonEdit_Profile -> doEditProfile()
             R.id.buttonLogout_Profile -> doLogout(view)
             R.id.mapsButton_Profile -> openMaps(view)
             R.id.instrument_button_Profile -> openInstruments(view)
             R.id.gen_button_Profile -> openGenres(view)
             R.id.selectPhoto_button_Profile -> loadImageFromGallery(view)//AGGIUNTO
         }
     }

     private fun doEditProfile() {
         when(btnEditProfile.text.toString()) {
             getString(R.string.save) -> saveProfile()
             getString(R.string.edit_profile) -> switchEditable(true)
         }
     }

     private fun saveProfile() {
         switchEditable(false)

         //TODO(load new values on firebase)
     }

     private fun switchEditable(modifiable: Boolean) {
         nickname.isEnabled = modifiable
         name.isEnabled = modifiable
         surname.isEnabled = modifiable
         city.isEnabled = modifiable
         mail.isEnabled = modifiable
         password.isEnabled = modifiable
         when(modifiable){
             true -> {
                 btnPhoto.isClickable = true
                 btnEditProfile.text = getString(R.string.save)
             }
             false -> {
                 btnPhoto.isClickable = false
                 btnEditProfile.text = getString(R.string.edit_profile)
             }
         }
     }

     private fun loadProfileFromFirebase(){
         switchEditable(false)
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
                 password.setText("********")
                 Picasso.get().load(user?.profile_image_url).into(profilePhoto)
                 btnPhoto.alpha = 0F
             }
             override fun onCancelled(error: DatabaseError) {
             }
         })
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

     private fun doLogout(view: View) {
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