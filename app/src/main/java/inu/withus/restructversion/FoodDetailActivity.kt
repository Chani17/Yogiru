package inu.withus.restructversion

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import inu.withus.restructversion.dto.FoodDTO
import inu.withus.restructversion.dto.RecyclerFoodInfoDTO

class FoodDetailActivity : AppCompatActivity() {

    lateinit var fdRecyclerAdaper: FoodDetailRecyclerAdapter
    private lateinit var foodDetailData: RecyclerFoodInfoDTO  //하나의 식품명에 대한 전체 데이터 받아오기
    private val foodDeatilDatas = mutableListOf<FoodDTO>()  // 하나의 식품에서 유통기한 다른 데이터 각각 분리한 데이터
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init{
        instance = this
    }

    companion object{
        private var instance:FoodDetailActivity? = null
        fun getInstance(): FoodDetailActivity? {
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)

        val intent: Intent = getIntent()
        foodDetailData = intent.getParcelableExtra("data")!!
        //var datas : FoodInfoDTO? = intent.getParcelableExtra("data")
        Log.d("TAG", "받아온 데이터 ___________ +$foodDetailData")

        val text: TextView = findViewById(R.id.foodName)
        text.text = foodDetailData?.foodName

        val dataLen = (foodDetailData.expireDate)?.size!!
        Log.d("TAG", "data 길이 : $dataLen")

        Log.d("TAG", "데이터 : $foodDeatilDatas")
        //리스트 형태로 들어있는 데이터 쪼개기
        for (i in 0..dataLen - 1) {
            val data = FoodDTO(
                place = foodDetailData.place,
                foodName = foodDetailData.foodName!!,
                expireDate = foodDetailData.expireDate!!.get(i),
                count = foodDetailData.count!!.get(i)
            )
            foodDeatilDatas.add(data)
        }
        initRecyclerview()
    }


    // 마이너스 버튼 클릭 시 개수 줄이기 =>
    fun reduceFoodData(data : FoodDTO) {
        if(data.count == 1){
            deleteFoodDataAll(data)
        }
        else{
            data.count = data.count!! - 1
            Log.d("TAG", " data 마이너스 1한 이후 데이터 확인 $foodDeatilDatas")
            // 데이터 클래스의 count 수 수정해주고  데이터 전체 삭제하고 다시 수정된거 추가
            deleteFoodDB(data)
            updateInfo(foodDeatilDatas)
        }
        fdRecyclerAdaper.notifyDataSetChanged()
    }

    // 휴지통 버튼 클릭 시 유통기한이 같은 한 식품 전체 삭제 ex) 2022-02-01까지인 사과가 3개 있다면 3개 동시에 삭제
    // 홈이랑 리스트 상단에 전체 개수도 줄여줘야 함!!
    fun deleteFoodDataAll(data : FoodDTO){
        Log.d("TAG", "삭제 함수 들어옴 data : $data")

        deleteFoodDB(data)

        if(foodDeatilDatas.size == 1) {
            foodDeatilDatas.remove(data)
            reduceCount(data.place!!)
            fdRecyclerAdaper.notifyDataSetChanged()
            finish()
        }

        else {
            foodDeatilDatas.remove(data)
            updateInfo(foodDeatilDatas)
            fdRecyclerAdaper.notifyDataSetChanged()
            initRecyclerview()
        }
    }


    //recyclerview 초기 설정
    private fun initRecyclerview() {
        fdRecyclerAdaper = FoodDetailRecyclerAdapter()
        fdRecyclerAdaper.replaceList(foodDeatilDatas)

        val foodDetailRecyclerView : RecyclerView = findViewById(R.id.foodDetailRecyclerView)
        foodDetailRecyclerView.adapter = fdRecyclerAdaper
        foodDetailRecyclerView.layoutManager = LinearLayoutManager(this)
    }



    // count 개수 줄이기
    private fun reduceCount(place : String) {
        val count = firestore?.collection("places")!!.document("count")

        when (place) {
            "냉장" -> {
                count.update("count_fridge", FieldValue.increment(-1))
                Log.d(ContentValues.TAG, "냉장 db 성공")
            }
            "냉동" -> {
                count.update("count_frozen", FieldValue.increment(-1))
                Log.d(ContentValues.TAG, "냉동 db 성공")
            }
            "실온" -> {
                count.update("count_room", FieldValue.increment(-1))
                Log.d(ContentValues.TAG, "실온 db 성공")
            }
        }
        return
    }


    //데이터 새로 추가
    private fun updateInfo(datas : MutableList<FoodDTO>) {
        val len = datas.size
        val place = datas.get(0).place!!
        val foodName = datas.get(0).foodName!!

        var setDate = hashMapOf(
            "place" to place,
            "foodName" to foodName
        )

        var countList = ArrayList<Int>()
        var expireDateList = ArrayList<String>()
        var memoList = ArrayList<String>()


        for(i in 0..len-1) {
            countList.add(datas.get(i).count!!)

            Log.d("TAG", "count 데이터 확인 : ${datas.get(i).count}")
            expireDateList.add(datas.get(i).expireDate!!)
            if(datas.get(i).memo!=null){
                memoList.add(datas.get(i).memo!!)
            }

        }

//        val docData = hashMapOf(
//            "count" to countList,
//            "expireDate" to expireDateList,
//            "memo" to memoList
//        )

        Log.d("TAG", "데이터 수정 시작")
        firestore?.collection(place)?.document(foodName)
            ?.set(setDate)
            ?.addOnSuccessListener {
                val document = firestore?.collection(place)?.document(foodName)!!
                document.update("foodName", foodName)
                document.update("count", countList)
                document.update("expireDate", expireDateList)
                document.update("memo", memoList)
            }

        return
    }

    private fun deleteFoodDB(data : FoodDTO){
        firestore?.collection(data.place!!)!!.document(data.foodName!!).delete()
            .addOnCompleteListener { task->
                if (task.isSuccessful) {
                    Log.d("FoodDetailActivity", "삭제완료")
                }
            }
    }

}

