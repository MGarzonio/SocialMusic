package it.uninsubria.socialmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner

class UsersProfileActivity : AppCompatActivity() {

    private lateinit var genres : Spinner
    private lateinit var instrument: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_profile)

        genres = findViewById(R.id.gen_spinner_UsersProfile)
        instrument = findViewById(R.id.instrument_spinner_UsersProfile)

        val instrumentAdapter = ArrayAdapter(this, R.layout.color_spinner_layout, getUserInstruments())
        val genresAdapter = ArrayAdapter(this, R.layout.color_spinner_layout, getUserGenres())

        genres.adapter = genresAdapter
        instrument.adapter = instrumentAdapter
    }

    private fun getUserGenres() : ArrayList<String> {
        var list = ArrayList<String>()
        //TODO load from firebase
        return list
    }

    private fun getUserInstruments() : ArrayList<String> {
        var list = ArrayList<String>()
        //TODO load from firebase
        return list
    }

    fun openChat(view: View) {

    }

}