package it.uninsubria.socialmusic.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profile_image_url: String, val name: String, val surname: String, val location: String, val instruments: String, val genres: String, val verified: String)
    : Parcelable{
    constructor(): this("","","", "","","","","", "")
}