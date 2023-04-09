package inu.withus.restructversion.cloudmessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import inu.withus.restructversion.MainActivity
import inu.withus.restructversion.R

class PushFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        sendRegisterationToServer(token) // token을 서버로 전송
    }

    private fun sendRegisterationToServer(token: String) {
    }

    // 메시지를 수신하는 메서드
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.data.isEmpty()) {
            sendNotification(
                remoteMessage.data["title"].toString(),
                remoteMessage.data["body"].toString()
            )
        } else {
            remoteMessage.notification?.let {
                sendNotification(
                    remoteMessage.notification!!.title.toString(),
                    remoteMessage.notification!!.body.toString()
                )
            }
        }
    }

    // 알림을 생성하는 메서드
    private fun sendNotification(title: String, body: String) {
        val notifyId = (System.currentTimeMillis() / 7).toInt()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, notifyId, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "chani"
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(body)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificaitonManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelId, NotificationManager.IMPORTANCE_HIGH
            )
            notificaitonManager.createNotificationChannel(channel)
        }

        notificaitonManager.notify(notifyId, notificationBuilder.build())
    }
}