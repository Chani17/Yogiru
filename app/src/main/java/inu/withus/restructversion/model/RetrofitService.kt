package inu.withus.restructversion.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// 결과 xml 파일에 접근해서 정보 가져오기
interface RetrofitService {
    @GET("recipe")
   fun getRecipe(
    ): Call<Recipe>
}