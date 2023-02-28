package com.flysolo.myapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Accounts(
    var id : String ? =null,
    val profile : String? =null,
    val name : String ? = null,
    val type : UserType ? = null,
    val email : String? =null,
) : Parcelable
