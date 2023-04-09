package inu.withus.restructversion.dao

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CountDAO(
    var count_fridge: Int? = null,
    var count_frozen: Int? = null,
    var count_room: Int? = null
)

