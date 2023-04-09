package inu.withus.restructversion

import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import inu.withus.restructversion.databinding.ActivityTimepickerBinding
import inu.withus.restructversion.dto.SettingTimeDTO
import java.util.*

class TimepickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimepickerBinding
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimepickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 시간 설정 후 확인 버튼을 누르면
        binding.ok.setOnClickListener {
            val mTimePicker = binding.timePicker
            val mCalendar = Calendar.getInstance()
            val hour = mTimePicker.hour
            val minute = mTimePicker.minute
            val settingTimeDTO = SettingTimeDTO()

            Log.d(TAG, "Hour = $hour")
            Log.d(TAG, "Minute = $minute")

            settingTimeDTO.hour = hour
            settingTimeDTO.minute = minute

            binding.timePicker.hour = settingTimeDTO.hour!!
            binding.timePicker.minute = settingTimeDTO.minute!!
            val result = settingTimeDTO.hour.toString() + "시" + settingTimeDTO.minute.toString() + "분"

            var pushCheck = intent.getBooleanExtra("pushCheck", false)
            Log.d(TAG, "pushCheck = $pushCheck")
            settingTimeDTO.pushCheck = pushCheck
            Log.d(TAG, "settingTimeDTO = $settingTimeDTO")

            // 설정한 시간 DB에 저장
            firestore.collection("Settings").document("test").set(settingTimeDTO)
            onBackPressed()

            binding.cancel.setOnClickListener {
                Toast.makeText(this, "TimePickerActivity 취소 후 종료", Toast.LENGTH_LONG).show()
                onBackPressed()
            }


        }
    }

}