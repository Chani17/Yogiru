package inu.withus.restructversion.dto

import com.google.firebase.database.annotations.NotNull
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class LoginDTO(
    @NotNull
    var Id: String? = null,

    @NotNull
    var password: String? = null
)

