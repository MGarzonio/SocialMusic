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
     private lateinit var address: EditText
     private lateinit var mail: EditText
     private lateinit var btnProfile: Button

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

         val view = inflater.inflate(R.layout.fragment_profile, container, false) as View
         val btnMap = view.findViewById(R.id.mapsButton) as ImageView
         val btnLogout = view.findViewById(R.id.buttonLogout) as Button
         val btnGenres = view.findViewById(R.id.buttonGenres) as Button
         val btnInstruments = view.findViewById(R.id.buttonInstruments) as Button

         nickname = view.findViewById(R.id.editTextNickname) as EditText
         name = view.findViewById(R.id.editTextName) as EditText
         surname = view.findViewById(R.id.editTextSurname) as EditText
         address = view.findViewById(R.id.editTextAddress) as EditText
         mail = view.findViewById(R.id.editTextEmail) as EditText
         btnProfile = view.findViewById(R.id.buttonEdit) as Button

         btnProfile.setOnClickListener(this)
         btnLogout.setOnClickListener(this)
         btnMap.setOnClickListener(this)
         btnGenres.setOnClickListener(this)
         btnInstruments.setOnClickListener(this)

         loadProfile()

         return view
     }

     override fun onClick(view: View) {
         when (view.id) {
             R.id.buttonEdit -> doEditProfile()
             R.id.buttonLogout -> doLogout(view)
             R.id.mapsButton -> openMaps(view)
             R.id.buttonInstruments -> openInstruments(view)
             R.id.buttonGenres -> openGenres(view)
         }
     }

     private fun doEditProfile() {
         when(btnProfile.text.toString()) {
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
         address.isEnabled = modifiable
         mail.isEnabled = modifiable
         when(modifiable){
             true -> btnProfile.text = getString(R.string.save)
             false ->  btnProfile.text = getString(R.string.edit_profile)
         }
     }

     private fun loadProfile() {

         switchEditable(false)

         //TODO load data from firebase with user's email as key

         nickname.setText("Garzu")
         name.setText("Mattia")
         surname.setText("Garzonio")
         address.setText("Via Don Selva 9, Somma Lombardo")
         mail.setText("mgarzonio@studenti.uninsubria.it")
     }

     private fun openGenres(view: View) {
         /*val intent = Intent(activity, GenresActivity::class.java)
         intent.putExtra("mail", mail.text.toString())
         startActivity(intent)*/
     }

     private fun openInstruments(view: View) {
         /*val intent = Intent(activity, InstrumentsActivity::class.java)
         intent.putExtra("mail", mail.text.toString())
         startActivity(intent)*/
     }

     private fun openMaps(view: View) {
         val intent = Intent(activity, MapsActivity::class.java)
         intent.putExtra("address", address.text.toString())
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