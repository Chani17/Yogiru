package inu.withus.restructversion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import inu.withus.restructversion.dao.CountDAO
import inu.withus.restructversion.dao.SettingTimeDAO
import inu.withus.restructversion.databinding.ActivityMainBinding
import inu.withus.restructversion.dto.SettingTimeDTO
import inu.withus.restructversion.dto.RecyclerFoodInfoDTO
import inu.withus.restructversion.notification.AlarmReceiver
import inu.withus.restructversion.util.FcmPush
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var recyclerAdaper: RecyclerAdapter
    private val datas = mutableListOf<RecyclerFoodInfoDTO>()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var auth: FirebaseAuth? = null
    val countDAO = CountDAO()
    var fcmPush: FcmPush? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
//        fetchDataFromSharedPreferences()

        fcmPush = FcmPush()
//        pushAlarm(fcmPush!!)

        // 최초 실행 시에만 해야 할 작업들을 작성
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Initialize And Assign Variable
        val bottomNavigation = binding.bottomNavigation
        setContentView(binding.root)

        val settingTimeDTO = SettingTimeDTO()
        settingTimeDTO.hour = 13
        settingTimeDTO.minute = 0
        settingTimeDTO.pushCheck = false

        firestore.collection("Settings").document("test").set(settingTimeDTO)
        // FCM 설정, Token 값 가져오기
//        PushNotificationService().getFirebaseToken()

        // DynamicLink
//        initDynamicLink()

        // Set Home Selected
        bottomNavigation.selectedItemId = R.id.home

        // Perform ItemSelectedListener
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.store -> startActivity(Intent(this, StoreActivity::class.java))
                R.id.shoppinglist -> startActivity(Intent(this, ShoppingListActivity::class.java))
                R.id.recipe -> startActivity(Intent(this, RecipeRecommendationActivity::class.java))
                R.id.setting -> startActivity(Intent(this, SettingActivity::class.java))
            }
            true
        }

        firestore.collection("places")!!.document("count")
            .addSnapshotListener { value, error ->
                var itemCount = value!!.toObject(countDAO::class.java)
                binding.itemCount1.text = itemCount!!.count_fridge.toString()
                binding.itemCount2.text = itemCount!!.count_frozen.toString()
                binding.itemCount3.text = itemCount!!.count_room.toString()
            }

        binding.addFood.setOnClickListener {
            val intent = Intent(this, RegisterFoodActivity::class.java)
            startActivity(intent)
        }

        val slidePanel = binding.slidingLayout
        val location = binding.location
        val count = binding.count


        //냉장, 냉동, 실온 버튼 클릭 시 리스트 출력
        binding.fridge.setOnClickListener{
            location.text = "냉장"
            count.text = binding.itemCount1.text
            initRecyclerData(location.text, "expireDate")
            if (slidePanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.anchorPoint = 0.7f
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }

            binding.sortByAlphabet.setOnClickListener{
                location.text = "냉장"
                count.text = binding.itemCount1.text
                initRecyclerData(location.text, "foodName")
            }

            binding.sortByDateAsc.setOnClickListener{
                location.text = "냉장"
                count.text = binding.itemCount1.text
                initRecyclerData(location.text, "expireDate" )
            }

            binding.sortByDateDesc.setOnClickListener{
                location.text = "냉장"
                count.text = binding.itemCount1.text
                initRecyclerData(location.text, "expireDate", Query.Direction.DESCENDING)
            }
        }

        binding.frozen.setOnClickListener{
            location.text = "냉동"
            count.text = binding.itemCount2.text
            initRecyclerData(location.text, "expireDate")
            if (slidePanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.anchorPoint = 0.7f
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }

            binding.sortByAlphabet.setOnClickListener{
                location.text = "냉동"
                count.text = binding.itemCount2.text
                initRecyclerData(location.text, "foodName")
            }

            binding.sortByDateAsc.setOnClickListener{
                location.text = "냉동"
                count.text = binding.itemCount2.text
                initRecyclerData(location.text, "expireDate")
            }

            binding.sortByDateDesc.setOnClickListener{
                location.text = "냉동"
                count.text = binding.itemCount2.text
                initRecyclerData(location.text, "expireDate", Query.Direction.DESCENDING)
            }
        }

        binding.room.setOnClickListener{
            location.text = "실온"
            count.text = binding.itemCount3.text
            initRecyclerData(location.text, "expireDate")
            if (slidePanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                slidePanel.anchorPoint = 0.7f
                slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
            }

            binding.sortByAlphabet.setOnClickListener{
                location.text = "실온"
                count.text = binding.itemCount3.text
                initRecyclerData(location.text, "foodName")
            }

            binding.sortByDateAsc.setOnClickListener{
                location.text = "실온"
                count.text = binding.itemCount3.text
                initRecyclerData(location.text, "expireDate")
            }

            binding.sortByDateDesc.setOnClickListener{
                location.text = "실온"
                count.text = binding.itemCount3.text
                initRecyclerData(location.text, "expireDate", Query.Direction.DESCENDING)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pushAlarm(fcmPush: FcmPush) {
//        val alarmDTO = AlarmDTO()
//        firestore?.collection("userInfo").document(auth?.uid!!).get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "destinationUid = ${task.result["pushToken"].toString()}")
//                    alarmDTO.destinationUid = task.result["pushToken"].toString()
//                    Log.d(TAG, "userId = ${task.result["userName"].toString()}")
//                    alarmDTO.userId = task.result["userName"].toString()
//                }
//            }

//        firestore?.collection("냉장").whereEqualTo("expireDate", LocalDate.now())
//            .addSnapshotListener { value, error ->
//                for (snapshot in value!!.documents) {
//                    if (snapshot["expireDate"] == LocalDate.now()) {
//                        var message = "안녕하세요" + alarmDTO.userId + "님"
//                        fcmPush?.sendMessage(auth?.uid!!, "Yogiru 알림 메시지입니다.", message)
//                    }
//                }


//        firestore?.collection("alarms").document().set(alarmDTO)

//                var message = "안녕하세요" + alarmDTO.userId + "님"
//                fcmPush?.sendMessage(auth?.uid!!, "Yogiru 알림 메시지입니다.", message)
//            }
    }
//    private fun registerPushToken() {
//        var uid = auth?.uid
//        Log.d(TAG, "함수 안 uid = $uid")
//        val map = mutableMapOf<String, Any>()
//
////        FirebaseFirestore.getInstance().collection("userInfo").document(uid!!).set(map)
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            // Log and toast
//            val pushToken = getString(R.string.msg_token_fmt, token)
//            Log.d(TAG, "pushToken = $pushToken")
//            map["pushToken"] = pushToken!!
//            map["userName"] = "지수"
//            Log.d(TAG, "map1 = ${map["pushToken"].toString()}")
//            Log.d(TAG, "map2 = ${map["userName"].toString()}")
//            firestore.collection("userInfo").document(uid!!).set(map)
//        })
//    }

//    /** DynamicLink */
//    private fun initDynamicLink() {
//        val dynamicLinkData = intent.extras
//        if (dynamicLinkData != null) {
//            var dataStr = "DynamicLink 수신받은 값\n"
//            for (key in dynamicLinkData.keySet()) {
//                dataStr += "key: $key / value: ${dynamicLinkData.getString(key)}\n"
//            }
//
//            Log.d(TAG, "토오큰 : $dataStr")
//        }
//    }

    //recyclerview 초기 설정
    private fun initRecyclerview() {
        recyclerAdaper = RecyclerAdapter(this)
        recyclerAdaper.replaceList(datas)
        Log.d("TAG", "init recyclerview as $datas")
        binding.recyclerView.adapter = recyclerAdaper
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }



    // recyclerview data 설정
    private fun initRecyclerData(location: CharSequence, sort :String, option : Query.Direction=Query.Direction.ASCENDING) {
        // DB에서 식품 리스트를 가지고 오는 부분
        Log.d(TAG, "들어옴!")

        firestore?.collection(location.toString())!!
            .orderBy(sort, option)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                removeAllData()
                for (document in querySnapshot!!.documents) {
                    val testData = document.toObject<RecyclerFoodInfoDTO>()!!
                    Log.d("TAG", "check datas : $testData")
                    datas.add(testData)
                    val cnt = testData.count
                    Log.d("TAG", "cnt : $cnt")
                }
                initRecyclerview()
            }
    }

    private fun removeAllData(){
        datas.clear()
        Log.d(TAG, "REMOVE : $datas")
    }

    // 뒤로가기 버튼 이벤트
    override fun onBackPressed() {
        val slidePanel = binding.slidingLayout
        val slideCollapsed = SlidingUpPanelLayout.PanelState.COLLAPSED
        val slideAnchored = SlidingUpPanelLayout.PanelState.ANCHORED
        val slideExpanded = SlidingUpPanelLayout.PanelState.EXPANDED

        if (slidePanel.panelState == slideExpanded || slidePanel.panelState == slideAnchored ) {
            slidePanel.panelState = slideCollapsed
        }

        else
            finish()
    }

}