package inu.withus.restructversion.dto

import com.google.firebase.database.annotations.NotNull
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ShoppingListDTO(

    @NotNull
    var foodName: String? = null,

    var memo: String? = null
)