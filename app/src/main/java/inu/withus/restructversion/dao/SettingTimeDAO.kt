package inu.withus.restructversion.dao

import com.google.firebase.database.annotations.NotNull
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class SettingTimeDAO(
    @NotNull
    var hour: Int = 13,

    @NotNull
    var minute: Int = 10,

    @NotNull
    var pushCheck: Boolean = false

)
