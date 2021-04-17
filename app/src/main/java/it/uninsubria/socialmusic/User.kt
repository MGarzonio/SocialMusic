package it.uninsubria.socialmusic

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profile_image_url: String): Parcelable{
    constructor(): this("","","")
}