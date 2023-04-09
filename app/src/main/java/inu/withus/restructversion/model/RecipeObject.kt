package inu.withus.restructversion.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecipeObject {
    private const val URL = "http://127.0.0.1:5000/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

//    val service: RetrofitService = retrofit.create(RetrofitService::class.java)
    fun getRetrofitService():RetrofitService {
        return retrofit.create(RetrofitService::class.java)
    }
}