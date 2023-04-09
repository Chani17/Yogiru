package inu.withus.restructversion.dto

import android.app.Notification

/**
 * to : PushToken 입력하는 부분 푸시를 받는 사용자
 * notification : 백드라운드 푸쉬 호출하는 변수
 * body : 백그라운드 푸시 메시지 내용
 * title : 백그라운드 푸시 타이틀
 */
data class PushDTO(var to:String? = null, var notification: Notification? = Notification()) {
    data class Notification(var body: String? = null, var title: String? = null)
}
