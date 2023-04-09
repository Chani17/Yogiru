package inu.withus.restructversion.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.FirebaseFirestore
import inu.withus.restructversion.R
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1000"
        const val NOTIFICATION_ID = 100
    }

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("AlarmReceiver", "AlarmReceiver 들어옴!")
        // 채널 생성
        createNotificationChannel(context)
        // 알림
        notifyNotification(context)

    }

    private fun createNotificationChannel(context: Context) {
        Log.d("AlarmReceiver", "createNotificationChannel 들어옴!")
        // context : 실행하고 있는 앱의 상태나 맥락을 담고 있음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "유통기한 표시",
                NotificationManager.IMPORTANCE_HIGH
            )

            NotificationManagerCompat.from(context)
                .createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context: Context) {
        var settingDate = 0
        Log.d("AlarmReceiver", "NotifiyNotification 들어옴!")
        firestore.collection("Settings").document("test").addSnapshotListener { value, error ->
            settingDate = value!!["date"].toString().toInt()
            Log.d("AlarmReceiver", "settingDate = $settingDate")
        }

        Log.d("AlarmReceiver", "settingDate = $settingDate")
        // 냉장 탐색
        firestore.collection("냉장").addSnapshotListener { value, error ->
            for(snapshot in value!!.documents) {
                var foodName = snapshot.getString("foodName")
                var cnt = snapshot["expireDate"] as ArrayList<String>?
                for(i in cnt!!) {
                    Log.d("AlarmReceiver", "i = $i")
                    val dDayCount = dDayCount(i)
                    if (dDayCount == settingDate) {
                        with(NotificationManagerCompat.from(context)) {
                            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                                .setContentTitle("유통기한 임박 알림!!")
                                .setContentText(foodName + "의 유통기한을 확인해보세요!")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)

                            notify(NOTIFICATION_ID, build.build())
                        }
                    }
                }

            }
        }

    }

    private fun dDayCount(expireDate : String) : Int{
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        val endDate = dateFormat.parse(expireDate).time

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        val dDay = ((endDate - today)/ (60 * 60 * 24 * 1000)).toInt()
        return dDay
    }
}