package inu.withus.restructversion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import inu.withus.restructversion.dao.SettingTimeDAO
import inu.withus.restructversion.notification.AlarmReceiver
import java.util.*

class SettingActivity : AppCompatActivity() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
    }

    override fun onPause() {
        super.onPause()
        fetchDataFromSharedPreferences()
    }

    private fun fetchDataFromSharedPreferences() {
        var alarmModel = SettingTimeDAO()
        firestore.collection("Settings").document("test").addSnapshotListener { value, error ->
            alarmModel = SettingTimeDAO(
                value!!["hour"].toString().toInt(),
                value!!["minute"].toString().toInt(),
                value!!["pushCheck"].toString().toBoolean()
            )
            Log.d("SettingActivity", "settingTimeDAO = $alarmModel")

            // 보정 조정 예외처 (브로드 캐스트 가져오기)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                M_ALARM_REQUEST_CODE,
                Intent(this, AlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE
            ) // 있으면 가져오고 없으면 안만든다. (null)
            Log.d("SettingActivity", "pendingIntent들어옴 = $pendingIntent")
            Log.d("SettingActivity", "settingTimeDAO2 = $alarmModel")

            if (alarmModel.pushCheck) {
                // 온 -> 알람을 등록
                Log.d("SettingActivity", "알람을 등록, 알람 On")
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, alarmModel.hour)
                    set(Calendar.MINUTE, alarmModel.minute)
                    // 지나간 시간의 경우 다음날 알람으로 울리도록
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1) // 하루 더하기
                        Log.d("SettingActivity", "하루 더하기")
                    }
                }

                //알람 매니저 가져오기.
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    M_ALARM_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                Log.d("SettingActivity", "알림 매니저 가져오고 pendingIntent = $pendingIntent")
                alarmManager.setInexactRepeating( // 정시에 반복
                    AlarmManager.RTC_WAKEUP, // RTC_WAKEUP : 실제 시간 기준으로 wakeup , ELAPSED_REALTIME_WAKEUP : 부팅 시간 기준으로 wakeup
                    calendar.timeInMillis, // 언제 알람이 발동할지.
                    AlarmManager.INTERVAL_DAY, // 하루에 한번씩.
                    pendingIntent
                )
                Log.d("SettingActivity", "알람 매니저 정시에 반복")
            } else {
                // 오프 -> 알람을 제거
                cancelAlarm()
                Log.d("SettingActivity", "알람 오프, 알람을 제거, Off")
            }
        }
    }


    private fun cancelAlarm() {
        // 기존에 있던 알람을 삭제한다.
        Log.d("SettingActivity", "cancelAlarm으로 들어옴")
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            M_ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE
        ) // 있으면 가져오고 없으면 안만든다. (null)

        Log.d("SettingActivity", "기존 알람 삭제")
        pendingIntent?.cancel() // 기존 알람 삭제
    }

    companion object {
        // static 영역 (상수 지정)
        private const val M_SHARED_PREFERENCE_NAME = "time"
        private const val M_ALARM_KEY = "alarm"
        private const val M_AMPM_KEY = "amPm"
        private val M_ALARM_REQUEST_CODE = 1000
    }

}