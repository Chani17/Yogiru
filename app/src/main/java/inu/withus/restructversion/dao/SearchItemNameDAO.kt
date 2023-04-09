package inu.withus.restructversion.dao

import android.os.Parcelable
import com.google.firebase.database.annotations.NotNull
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
data class SearchItemNameDAO(

    @NotNull
    var foodName: String? = null,

    @NotNull
    var foodCount: Int? = null
)