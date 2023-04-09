package inu.withus.restructversion.model

import com.google.gson.annotations.SerializedName
import org.tensorflow.lite.DataType

// 레시피 정보를 담는 데이터 클래스
data class RecipeModel(

//    @SerializedName("number") var number : Int = 0,
//    @SerializedName("ingredient") var ingredient: String,
    @SerializedName("recipe_name") var recipeName: String = "",
    @SerializedName("score") var score: Double = 0.0

)

data class Recipe(val response: RESPONSE)
data class RESPONSE(val header: HEADER, val body: BODY)
data class HEADER(val resultCode : Int, val resultMsg: String)
data class BODY(val dataType: String, val items: ITEMS)
data class ITEMS(val item:List<ITEM>)
data class ITEM(val recipeName: String, val score: Double)

