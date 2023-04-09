package inu.withus.restructversion.dto

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.annotations.NotNull

@IgnoreExtraProperties
data class NotificationDTO(
    @NotNull
    var foodName: String? = null,

    @NotNull
    var date: Int? = null
)
