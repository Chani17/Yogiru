package inu.withus.restructversion.dto

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.annotations.NotNull
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class RecyclerFoodInfoDTO(
    @NotNull
    var place : String? = null,

    @NotNull
    var foodName : String? = null,

    @NotNull
    var expireDate : List<String>? = null,

    @NotNull
    var count : List<Int>? = null,

    var memo : List<String>? = null
):Parcelable
