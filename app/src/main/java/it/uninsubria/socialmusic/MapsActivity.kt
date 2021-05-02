package it.uninsubria.socialmusic

import android.content.Context
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*
import javax.xml.transform.dom.DOMLocator
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var address: String? = null
    private var nickname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        address = intent.getStringExtra("city")
        nickname = intent.getStringExtra("nickname")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val myPosition = getPosition(this, address!!)
        if(myPosition != null) {
            mMap.addMarker(MarkerOptions().position(myPosition).title(nickname))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 13.0f))
        }
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
}