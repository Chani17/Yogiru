package inu.withus.restructversion.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import inu.withus.restructversion.dto.PushDTO
import okhttp3.*
import java.io.IOException

class FcmPush {
    val JSON = MediaType.parse("application/json; charset=utf-8")
    val url = "https://fcm.googleapis.com/fcm/send"
    val serverKey = "AAAAsXoZs2M:APA91bE2w7uD-_uluNHd_dc78q0EPBxnOHclCgTKQYvEYW_SVvGb_0JKsDeNzrc0EV6wy4UxbStdmmKwS2sqvbUsZzckZYZwKI7qCMkXPYkjDK2jAnwMrYNPAasoDpVjfPc3LO4nTSBh"

    var okHttpClient: OkHttpClient? = null
    var gson: Gson? = null
    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("userInfo").document(destinationUid)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result["pushToken"].toString()

                    var pushDTO = PushDTO()
                    pushDTO.to = token
                    pushDTO.notification?.title = title
                    pushDTO.notification?.body = message
                    var body = RequestBody.create(JSON, gson?.toJson(pushDTO))
                    var request = Request
                        .Builder()
                        .addHeader("Content-type", "application/json")
                        .addHeader("Authorization", "key=$serverKey")
                        .url(url)
                        .post(body)
                        .build()

                    okHttpClient?.newCall(request)?.enqueue(object : Callback {
                        // push 전송
                        override fun onFailure(call: Call?, e: IOException?) {

                        }

                        override fun onResponse(call: Call?, response: Response?) {
                            // 요청이 성공했을 경우 결과값 출력
                            println("요청 성공!" + response?.body()?.string())
                        }
                    })
                }
            }
    }
}