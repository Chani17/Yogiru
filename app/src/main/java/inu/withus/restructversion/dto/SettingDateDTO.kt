package inu.withus.restructversion.dto

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.annotations.NotNull

@IgnoreExtraProperties
class SettingDateDTO {
    @NotNull
    var date: Int? = null
}