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
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var address: String? = null
    private var nickname: String? = null
    private var longitude by Delegates.notNull<Double>()
    private var latitude by Delegates.notNull<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        address = intent.getStringExtra("address");
        nickname = intent.getStringExtra("nickname");
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getCoordinates(this)
        val myPosition = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(myPosition).title(nickname))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 13.0f));
    }

    private fun getCoordinates(context: Context) {
        var addressList = Geocoder(context, Locale.getDefault()).getFromLocationName(address, 1)
        if (addressList != null && addressList.size > 0) {
            val address = addressList[0];
            longitude = address.longitude;
            latitude = address.latitude;
        }
    }
}