package inu.withus.restructversion.dto

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.annotations.NotNull

@IgnoreExtraProperties
data class FoodInfoDTO(
    @NotNull
    var place : String? = null,

    @NotNull
    var foodName : String? = null,

//    @NotNull
//    var expireDate : String? = null,
//
//    @NotNull
//    var count : Int? = null,
//
//    var memo : String? = " "
)