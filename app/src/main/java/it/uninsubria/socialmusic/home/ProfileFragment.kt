 package it.uninsubria.socialmusic.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.socialmusic.*

 class ProfileFragment : Fragment(), View.OnClickListener {

     private lateinit var nickname: EditText
     private lateinit var name: EditText
     private lateinit var surname: EditText
     private lateinit var city: EditText
     private lateinit var mail: EditText
     private lateinit var btnEditProfile: Button
     private lateinit var btnPhoto : Button

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

         val view = inflater.inflate(R.layout.fragment_profile, container, false) as View
         val btnMap = view.findViewById(R.id.mapsButton) as ImageView
         val btnLogout = view.findViewById(R.id.buttonLogout) as Button
         val btnGenres = view.findViewById(R.id.gen_button_Profile) as Button
         val btnInstruments = view.findViewById(R.id.instrument_button_Profile) as Button

         nickname = view.findViewById(R.id.nickname_editText_Profile) as EditText
         name = view.findViewById(R.id.name_editText_Profile) as EditText
         surname = view.findViewById(R.id.surname_editText_Profile) as EditText
         city = view.findViewById(R.id.location_editText_Profile) as EditText
         mail = view.findViewById(R.id.editTextEmail) as EditText
         btnEditProfile = view.findViewById(R.id.buttonEdit) as Button
         btnPhoto = view.findViewById(R.id.selectPhoto_button_Profile) as Button

         btnEditProfile.setOnClickListener(this)
         btnLogout.setOnClickListener(this)
         btnMap.setOnClickListener(this)
         btnGenres.setOnClickListener(this)
         btnInstruments.setOnClickListener(this)
         btnPhoto.setOnClickListener(this)

         loadProfile()

         return view
     }

     override fun onClick(view: View) {
         when (view.id) {
             R.id.buttonEdit -> doEditProfile()
             R.id.buttonLogout -> doLogout(view)
             R.id.mapsButton -> openMaps(view)
             R.id.instrument_button_Profile -> openInstruments(view)
             R.id.gen_button_Profile -> openGenres(view)
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

         //TODO load new values on firebase
     }

     private fun switchEditable(modifiable: Boolean) {
         nickname.isEnabled = modifiable
         name.isEnabled = modifiable
         surname.isEnabled = modifiable
         city.isEnabled = modifiable
         mail.isEnabled = modifiable
         when(modifiable){
             true -> {
                 btnPhoto.visibility = View.GONE

                 btnEditProfile.text = getString(R.string.save)
             }
             false -> {
                 btnPhoto.visibility = View.VISIBLE
                 btnEditProfile.text = getString(R.string.edit_profile)
             }
         }
     }

     private fun loadProfile() {

         switchEditable(false)

         //TODO load data from firebase with user's email as key

         nickname.setText("Garzu")
         name.setText("Mattia")
         surname.setText("Garzonio")
         city.setText("Somma Lombardo")
         mail.setText("mgarzonio@studenti.uninsubria.it")
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

 }