package inu.withus.restructversion.dto

import com.google.firebase.database.annotations.NotNull
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class SettingTimeDTO(
    @NotNull
    var hour: Int? = null,

    @NotNull
    var minute: Int? = null,

    var pushCheck: Boolean = false
)
