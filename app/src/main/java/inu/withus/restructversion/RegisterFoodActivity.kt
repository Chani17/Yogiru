package inu.withus.restructversion

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import inu.withus.restructversion.databinding.ActivityMainBinding
import inu.withus.restructversion.dto.FoodInfoDTO
import inu.withus.restructversion.databinding.ActivityRegisterFoodBinding
import inu.withus.restructversion.databinding.StandardExpirationdateBinding
import inu.withus.restructversion.dto.InfoListDTO
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class RegisterFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterFoodBinding
    private lateinit var dlgBinding: StandardExpirationdateBinding
    private var ingredents = arrayOf("사과", "바나나", "당근", "감자", "오이", "파프리카", "양파")
    private var average_exp = arrayOf("2주~1개월", "7일~10일", "2주~1개월", "1개월", "4~5일", "3~4주", "1주~2주")
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    private var place: String = "냉장"
    private var searchExist = false
    private val standardExpirationdateActivity = StandardExpirationdateActivity.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterFoodBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setContentView(binding.root)

        /* 냉장, 냉동, 실온 터치 이벤트 */

        binding.offButton1.setOnClickListener {
            binding.onButton1.visibility = View.VISIBLE
            binding.onButton2.visibility = View.INVISIBLE
            binding.onButton3.visibility = View.INVISIBLE
            binding.offButton1.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.offButton2.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            binding.offButton3.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            place = binding.offButton1.text.toString()
        }

        binding.offButton2.setOnClickListener {
            binding.onButton1.visibility = View.INVISIBLE
            binding.onButton2.visibility = View.VISIBLE
            binding.onButton3.visibility = View.INVISIBLE
            binding.offButton1.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            binding.offButton2.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.offButton3.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            place = binding.offButton2.text.toString()
        }

        binding.offButton3.setOnClickListener {
            binding.onButton1.visibility = View.INVISIBLE
            binding.onButton2.visibility = View.INVISIBLE
            binding.onButton3.visibility = View.VISIBLE
            binding.offButton1.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            binding.offButton2.setTextColor(ContextCompat.getColor(this, R.color.dark_gray))
            binding.offButton3.setTextColor(ContextCompat.getColor(this, R.color.black))
            place = binding.offButton3.text.toString()
        }

        binding.standardExpiredate.setOnClickListener {
            val dialog = StandardExpirationdateActivity(this)
            dialog.showDialog()
        }

        binding.mButton.setOnClickListener {
            val intent = Intent(this, CameraIntegrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "RegisterFoodActivity_name에 들어옴!")

        //식품 이름 가져오기 및 저장
        val name : TextView = findViewById(R.id.InputfoodName)
        var resultName = intent.getStringExtra("foodName")

        // 만약 이름이 아래의 영문에 해당한다면 한글로 바꾸기
        when(resultName){
            "apple" -> resultName =  "사과"
            "banana" -> resultName =  "바나나"
            "carrot" -> resultName = "당근"
            "potato" -> resultName = "감자"
            "cucumber" ->resultName = "오이"
            "paprika" -> resultName = "파프리카"
            "onion" -> resultName = "양파"
        }
        name.text = resultName

        Log.d(TAG, "RegisterFoodActivity_date에 들어옴!")
        val viewDate : TextView = findViewById(R.id.InputexpireDate)

        lateinit var resultExpireDate : String

        if(resultName!=null){

            if(resultName in ingredents)
                resultExpireDate =  returnExpireDate(resultName!!)
            else
                resultExpireDate = intent.getStringExtra("expireDate")!!
            viewDate.text = resultExpireDate

        }

        binding.register.setOnClickListener {
            //예외처리
            if(binding.InputfoodName.length()==0){
                Toast.makeText(getApplicationContext(),"식품명을 입력해주세요",Toast.LENGTH_LONG).show();
            }

            else if(binding.InputexpireDate.length()==0){
                Toast.makeText(getApplicationContext(),"유통기한을 입력해주세요",Toast.LENGTH_LONG).show();
            }

            else if(binding.InputCount.length()==0){
                Toast.makeText(getApplicationContext(),"수량을 입력해주세요",Toast.LENGTH_LONG).show();
            }

            else{
                val intent = Intent(this, MainActivity::class.java)
                val count = firestore?.collection("places")!!.document("count")
                var foodInfoDTO = FoodInfoDTO()
                var infoListDTO = InfoListDTO()

                checkDoc(name, viewDate, infoListDTO, foodInfoDTO, count)

                Toast.makeText(this, "저장 완료", Toast.LENGTH_LONG).show()
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

            }
        }

        binding.cancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun checkDoc(name: TextView, result: TextView, infoListDTO: InfoListDTO, foodInfoDTO: FoodInfoDTO, count: DocumentReference) {
        firestore?.collection(place)!!.whereEqualTo("foodName", name.text.toString().trim()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        updateInfo(result, name.text.toString(), place, infoListDTO)
                        searchExist = true
                        return@addOnCompleteListener
                    }
                }
                if (!searchExist) {
                    registerInfo(foodInfoDTO, infoListDTO, count, name, result)
                }
            }

    }

    private fun registerInfo(foodInfoDTO: FoodInfoDTO, infoListDTO: InfoListDTO, count: DocumentReference, name: TextView, result: TextView) {
            foodInfoDTO.place = place
            Log.d(TAG, "place = " + foodInfoDTO.place)
            foodInfoDTO.foodName = name.text.toString()
            Log.d(TAG, "name = " + foodInfoDTO.foodName)
            infoListDTO.expireDate = result.text.toString()
            infoListDTO.count = binding.InputCount.text.toString().toInt()
            infoListDTO.memo = binding.InputMemo.text.toString()

            firestore?.collection(place)?.document(foodInfoDTO.foodName!!)
                ?.set(foodInfoDTO)
                ?.addOnSuccessListener {
                    val document =
                        firestore?.collection(place)!!.document(foodInfoDTO.foodName!!)
                    document.update(
                        "expireDate",
                        FieldValue.arrayUnion(infoListDTO.expireDate)
                    )
                    document.update("count", FieldValue.arrayUnion(infoListDTO.count))
                    document.update("memo", FieldValue.arrayUnion(infoListDTO.memo))

                    when (place) {
                        "냉장" -> {
                            count.update("count_fridge", FieldValue.increment(1))
                            Log.d(TAG, "냉장 db 성공")
                        }
                        "냉동" -> {
                            count.update("count_frozen", FieldValue.increment(1))
                            Log.d(TAG, "냉동 db 성공")
                        }
                        "실온" -> {
                            count.update("count_room", FieldValue.increment(1))
                            Log.d(TAG, "실온 db 성공")
                        }
                    }
            Log.d(TAG, "searchExist2 : $searchExist")
        }
       return
    }

    // 이미 DB에 존재하는 식품일 경우
    private fun updateInfo(
        result: TextView,
        foodName: String,
        place: String,
        infoListDTO: InfoListDTO
    ) {
        infoListDTO.expireDate = result.text.toString()
        infoListDTO.count = binding.InputCount.text.toString().toInt()
        infoListDTO.memo = binding.InputMemo.text.toString()

        Log.d("TAG", "searchExist 들어왔음")
        val document = firestore?.collection(place)!!.document(foodName!!)
        document.update("expireDate", FieldValue.arrayUnion(infoListDTO.expireDate))
        document.update("count", FieldValue.arrayUnion(infoListDTO.count))
        document.update("memo", FieldValue.arrayUnion(infoListDTO.memo))
    }

    private fun returnExpireDate(resultName : String) : String{
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        val calendar = Calendar.getInstance()
        calendar.setTime(date)
        when(resultName){
            "사과" -> {calendar.add(Calendar.DATE, 14)}
            "바나나" -> {calendar.add(Calendar.DATE, 7)}
            "당근" -> {calendar.add(Calendar.DATE, 14)}
            "감자" -> {calendar.add(Calendar.DATE, 30)}
            "오이" -> {calendar.add(Calendar.DATE, 4)}
            "파프리카" -> {calendar.add(Calendar.DATE, 21)}
            "양파" -> {calendar.add(Calendar.DATE, 7)}

        }
        val time = calendar.time
        val formatTime = dateFormat.format(time)
        val resultExpireDate : String = formatTime

        return resultExpireDate
    }


    override fun onStop() {
        super.onStop()
    }
}