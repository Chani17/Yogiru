//package inu.withus.restructversion.cloudmessaging;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import inu.withus.restructversion.MainActivity;
//import inu.withus.restructversion.R;
//// https://github.com/firebase/quickstart-android/blob/f09bc36434e1bfbfec723f66ecfe302412b4a41d/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/kotlin/MyFirebaseMessagingService.kt
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        if(remoteMessage.getData().size() > 0) {
//            Log.d("chani", "Message data payload: " + remoteMessage.getData());
//
//            if(false) {
//                // For Long-running tasks(10 seconds or more) use WorkManager
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
//            // Check if message contains a notification payload.
//            if(remoteMessage.getNotification() != null) {
//                Log.d("chani", "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            }
//        }
//    }
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//        super.onNewToken(token);
//
//        Log.d("chani", "onNewToken token =  " + token);
//        sendRegistrationToServer(token);
//    }
//
//    private void scheduleJob() {
//        Log.d("chani", "scheduleJob in");
//    }
//
//    private void handleNow() {
//        Log.d("chani", "handleNow in");
//    }
//
//    private void sendRegistrationToServer(String token) {
//        Log.d("chani", "onNewToken token = " + token);
//    }
//
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = "1000";
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat().Builder(this, channelId)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//
//
//}
