 package it.uninsubria.socialmusic

import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_profile.*

 class ProfileFragment : Fragment(), View.OnClickListener {

     private lateinit var nickname: EditText
     private lateinit var name: EditText
     private lateinit var surname: EditText
     private lateinit var address: EditText
     private lateinit var mail: EditText
     private lateinit var btnProfile: Button
     private lateinit var btnGenres: ImageView
     private lateinit var btnInstruments: ImageView
     private lateinit var instrumentList: Spinner
     private lateinit var genresList: Spinner

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

         val view = inflater.inflate(R.layout.fragment_profile, container, false)
         val btnLogout = view.findViewById<Button>(R.id.buttonLogout)
         val btnMap = view.findViewById<ImageView>(R.id.mapsButton)

         nickname = view.findViewById(R.id.editTextNickname)
         name = view.findViewById(R.id.editTextName)
         surname = view.findViewById(R.id.editTextSurname)
         address = view.findViewById(R.id.editTextAddress)
         mail = view.findViewById(R.id.editTextEmail)
         btnProfile = view.findViewById(R.id.buttonEdit)
         btnGenres = view.findViewById(R.id.editGenres)
         btnInstruments = view.findViewById(R.id.editInstruments)
         instrumentList = view.findViewById(R.id.spinnerInstruments)
         genresList = view.findViewById(R.id.spinnerGenres)

         btnProfile.setOnClickListener(this)
         btnLogout.setOnClickListener(this)
         btnMap.setOnClickListener(this)
         btnGenres.setOnClickListener(this)
         btnInstruments.setOnClickListener(this)

         loadProfile(view.context)

         return view
     }

     override fun onClick(view: View) {
         when (view.id) {
             R.id.buttonEdit -> doEditProfile()
             R.id.buttonLogout -> doLogout(view)
             R.id.mapsButton -> openMaps(view)
             R.id.editInstruments -> changeInstruments()
             R.id.editGenres -> changeGenres()
         }
     }

     private fun openMaps(view: View) {
         val intent = Intent(activity, MapsActivity::class.java)
         intent.putExtra("address", address.text.toString())
         intent.putExtra("nickname", nickname.text.toString())
         startActivity(intent)
     }

     private fun doEditProfile() {
         when(btnProfile.text.toString()) {
             getString(R.string.save) -> switchEditable(false)
             getString(R.string.edit_profile) -> switchEditable(true)
         }
     }

     private fun switchEditable(modifiable: Boolean) {
         nickname.isEnabled = modifiable
         name.isEnabled = modifiable
         surname.isEnabled = modifiable
         address.isEnabled = modifiable
         mail.isEnabled = modifiable
         when(modifiable){
             true -> {
                 btnProfile.text = getString(R.string.save)
                 btnGenres.visibility = View.VISIBLE
                 btnInstruments.visibility = View.VISIBLE
             }
             false -> {
                 btnProfile.text = getString(R.string.edit_profile)
                 btnGenres.visibility = View.GONE
                 btnInstruments.visibility = View.GONE
             }
         }
     }

     private fun loadProfile(context: Context) {

         switchEditable(false)

         //TODO() load data from firebase

         val adapterGenres = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayOf<String>("Death metal", "Trash metal", "Heavy metal"))
         val adapterInstruments = ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayOf<String>("Drum", "Electric triangle"))

         adapterGenres.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
         adapterInstruments.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

         //spinnerGenres.adapter = adapterGenres
         //spinnerInstruments.adapter = adapterInstruments

         nickname.setText("Garzu")
         name.setText("Mattia")
         surname.setText("Garzonio")
         address.setText("Via Don Selva 9, Somma Lombardo")
         mail.setText("mgarzonio@studenti.uninsubria.it")
     }

     private fun changeGenres(){

     }

     private fun changeInstruments(){

     }

     private fun doLogout(view: View) {
         val intent = Intent(activity, MainActivity::class.java)
         startActivity(intent)
     }
 }