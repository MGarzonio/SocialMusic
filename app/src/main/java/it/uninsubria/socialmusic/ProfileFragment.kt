 package it.uninsubria.socialmusic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(), View.OnClickListener {

    private var editable = false
    private lateinit var nickname : EditText
    private lateinit var name : EditText
    private lateinit var surname : EditText
    private lateinit var address : EditText
    private lateinit var mail : EditText


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val btnLogout = view.findViewById<Button>(R.id.buttonLogout)
        val btnEdit = view.findViewById<Button>(R.id.buttonEdit)

        nickname = view.findViewById<EditText>(R.id.editTextNickname)
        name = view.findViewById<EditText>(R.id.editTextName)
        surname = view.findViewById<EditText>(R.id.editTextSurname)
        address = view.findViewById<EditText>(R.id.editTextAddress)
        mail = view.findViewById<EditText>(R.id.editTextEmail)
        btnEdit.setOnClickListener(this)
        btnLogout.setOnClickListener(this)
        
        switchEditable()

        return view
    }

    override fun onClick(v: View?) {
        val btn : Button
        when (v?.id) {
            R.id.buttonEdit -> {
                btn = (view?.findViewById<Button>(R.id.buttonEdit)) as Button
                if(editable) {   // click save
                    btn.text = "Edit profile"
                    editable = false
                    switchEditable()
                }
                else{           //click edit
                    btn.text = "Save"
                    editable = true
                    switchEditable()
                }
                doEditProfile()
            }
            R.id.buttonLogout -> doLogout()
        }
    }

    private fun doEditProfile(){
        editTextNickname
    }

    private fun switchEditable(){
        nickname.isFocusable = editable
        name.isFocusable = editable
        surname.isFocusable = editable
        address.isFocusable = editable
        mail.isFocusable = editable
    }

    private fun doLogout(){

        TODO()

    }

}