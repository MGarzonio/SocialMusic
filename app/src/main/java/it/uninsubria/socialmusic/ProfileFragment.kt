 package it.uninsubria.socialmusic

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

 class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var nickname : EditText
    private lateinit var name : EditText
    private lateinit var surname : EditText
    private lateinit var address : EditText
    private lateinit var mail : EditText
    private lateinit var edit : Button
    private var editable = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val btnLogout = view.findViewById<Button>(R.id.buttonLogout)
        val btnMap = view.findViewById<ImageView>(R.id.mapsButton)

        nickname = view.findViewById<EditText>(R.id.editTextNickname)
        name = view.findViewById<EditText>(R.id.editTextName)
        surname = view.findViewById<EditText>(R.id.editTextSurname)
        address = view.findViewById<EditText>(R.id.editTextAddress)
        mail = view.findViewById<EditText>(R.id.editTextEmail)
        edit = view.findViewById<Button>(R.id.buttonEdit)

        edit.setOnClickListener(this)
        btnLogout.setOnClickListener(this)
        btnMap.setOnClickListener(this)

        loadProfile()

        return view
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonEdit -> doEditProfile()
            R.id.buttonLogout -> doLogout(view)
            R.id.mapsButton -> openMaps(view)
        }
    }

     private fun openMaps(view: View) {
         val intent = Intent(activity, MapsActivity::class.java)
         intent.putExtra("address", address.text.toString())
         intent.putExtra("nickname", nickname.text.toString())
         startActivity(intent)
     }

    private fun doEditProfile(){
        if (editable) {   // click save
            edit.text = getString(R.string.edit_profile)
            editable = false
            switchEditable()
        } else {           //click edit
            edit.text = getString(R.string.save)
            editable = true
            switchEditable()
        }
    }

    private fun switchEditable(){
        nickname.isEnabled = editable
        name.isEnabled = editable
        surname.isEnabled = editable
        address.isEnabled = editable
        mail.isEnabled = editable
    }

    private fun loadProfile(){
       switchEditable()
        nickname.setText("Garzu")
        name.setText("Mattia")
        surname.setText("Garzonio")
        address.setText("Via Don Selva 9, Somma Lombardo")
        mail.setText("mgarzonio@studenti.uninsubria.it")
    }

    private fun doLogout(view: View){
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
 }