package inu.withus.restructversion

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import inu.withus.restructversion.dao.SearchItemNameDAO
import inu.withus.restructversion.databinding.ActivityRecipeBinding
import inu.withus.restructversion.databinding.ActivityRecipeRecommendationBinding
import inu.withus.restructversion.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeRecommendationActivity : AppCompatActivity() {

    private var mBinding : ActivityRecipeRecommendationBinding? = null
    private val binding get() = mBinding!!

    /*
    //재료가 가장 많이 일치하는 레시피
    private var recipeCode_1 = 0 //레시피 코드
    private var recipeName_1 : String ?= " " //레시피 이름
    //private var recipeAllIngredient_1 : String ?= " " //전체 재료
    private var recipePartialIngredient_1 : String ?= " " //겹치는 재료
    private var recipeCount_1 = 0 //겹치는 재료의 개수
    //private var recipeProcess_1 : String ?= " " //조리 과정
    //재료가 두 번째로 많이 일치하는 레시피
    private var recipeCode_2 = 0 //레시피 코드
    private var recipeName_2 : String ?= " " //레시피 이름
    //private var recipeAllIngredient_2 : String ?= " " //전체 재료
    private var recipePartialIngredient_2 : String ?= " " //겹치는 재료
    private var recipeCount_2 = 0 //겹치는 재료의 개수
    //private var recipeProcess_2 : String ?= " " //조리 과정
    */

    /* 예시! 위에 주석 처리해 둔 각 파라미터에 값 넣어 준 뒤엔 윗 부분 주석 해제, 이 부분엔 주석 처리 해 주면 돼! */
    /* 전체 재료랑 제작 과정은 RecipeDialog.kt에서 연결! */
    /* 파라미터로 전달할 내용 너무 많아지면 안 좋을 것 같아서 레시피 코드만 전달하는 식으로 짰어 */
    //재료가 가장 많이 일치하는 레시피
    private var recipeCode_1 = 1 //레시피 코드
    private var recipeName_1 : String ?= "김치참치찌개" //레시피 이름
    //private var recipeAllIngredient_1 : String ?= " " //전체 재료
    private var recipePartialIngredient_1 : String ?= "#김치 #참치 #양파" //겹치는 재료
    private var recipeCount_1 = 3//겹치는 재료의 개수
    //private var recipeProcess_1 : String ?= " " //조리 과정

    //재료가 두 번째로 많이 일치하는 레시피
    private var recipeCode_2 = 2 //레시피 코드
    private var recipeName_2 : String ?= "돼지고기카레" //레시피 이름
    //private var recipeAllIngredient_2 : String ?= " " //전체 재료
    private var recipePartialIngredient_2 : String ?= "#돼지고기 #카레" //겹치는 재료
    private var recipeCount_2 = 2//겹치는 재료의 개수
    //private var recipeProcess_2 : String ?= " " //조리 과정

    private var recipeCode_3 = 3 //레시피 코드
    private var recipeName_3 : String ?= "계란볶음밥" //레시피 이름
    //private var recipeAllIngredient_2 : String ?= " " //전체 재료
    private var recipePartialIngredient_3 : String ?= "#계란 #당근 #파"  //겹치는 재료
    private var recipeCount_3 = 1//겹치는 재료의 개수
    var recipe : String ?= " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_recommendation)

        mBinding = ActivityRecipeRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // api 호출
        apiRequest()
//        val call = RecipeObject.getRetrofitService().getRecipe()
//        Log.d("getRecipe 가져와 제발", call.toString())
//
//        call.enqueue(object: retrofit2.Callback<Recipe> {
//            // 응답 성공시
//            override fun onResponse(call: Call<Recipe>, response: Response<Recipe>) {
//                if(response.isSuccessful) {
//                    // 레시피 이름과 스코어 가져오기
//                    val it:List<ITEM> = response.body()!!.response.body.items.item
//                    Log.d("음식 명 & 스코어 가져오기 성공", it.iterator().toString())
//                    val temp = RecipeModel()
//                    temp.recipeName = it[0].recipeName
//                    temp.score = it[0].score
//                    Log.d("요리 정보", "${temp.recipeName} + ${temp.score}")
//                }
//            }
//
//            override fun onFailure(call: Call<Recipe>, t: Throwable) {
//                Log.d("api fail", t.message.toString())
//            }
//        })

        //재료가 가장 많이 일치하는 레시피 정보 출력
        binding.recipeItem1Name.text = recipeName_1
        binding.recipeItem1Ingredient.text = recipePartialIngredient_1
        binding.recipeItem1Count.text = recipeCount_1.toString()

        //재료가 두 번째로 많이 일치하는 레시피 정보 출력
        binding.recipeItem2Name.text = recipeName_2
        binding.recipeItem2Ingredient.text = recipePartialIngredient_2
        binding.recipeItem2Count.text = recipeCount_2.toString()

        binding.recipeItem3Name.text = recipeName_3
        binding.recipeItem3Ingredient.text = recipePartialIngredient_3
        binding.recipeItem3Count.text = recipeCount_3.toString()

        //각 이미지 버튼 클릭 시 레시피 정보 담긴 다이얼로그 출력
        binding.recipeItem1.setOnClickListener {
            openRecipeDetail(recipeCode_1) //addItem 함수 실행
        }
        binding.recipeItem2.setOnClickListener {
            openRecipeDetail(recipeCode_2) //addItem 함수 실행
        }
        binding.recipeItem3.setOnClickListener {
            openRecipeDetail(recipeCode_3) //addItem 함수 실행
        }

    }

    /**
     * HTTP 호출
     */
    private fun apiRequest() {
        // 1. retrofit 객체 생성
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 2. service 객체 생성
        val apiService: ApiService = retrofit.create(ApiService::class.java)

        // 3. Call 객체 생성
        val tickerCall = apiService.getRecipeTicker()

        // 4. 네트워크 통신
        tickerCall.enqueue(object: Callback<Ticker> {
            override fun onResponse(call: Call<Ticker>, response: Response<Ticker>) {
                // 호출 데이터
                val tickerInfo = response.body()

                Log.d("recipeName", "${tickerInfo?.data?.recipe_name}")
                Log.d("ingredents", "${tickerInfo?.data?.ingredients}")
                Log.d("score", "${tickerInfo?.data?.score}")

            }

            override fun onFailure(call: Call<Ticker>, t: Throwable) {
                // 오류 시 발생
                call.cancel()
            }
        })
    }

    fun openRecipeDetail(code: Int){
        val dialog = RecipeDialog(this)
        dialog.showDialog(code)
    }
}