package com.example.notisticky.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.notisticky.MainActivity
import com.example.notisticky.R
import com.example.notisticky.data.local.MemoEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 시스템의 알림 매니저 소환
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "sticky_memo_channel"
        const val CHANNEL_NAME = "고정 메모"
    }

    // 초기화: 채널 만들기
    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "상단바에 고정되는 메모입니다."
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 알림 띄우기
    fun showNotification(memo: MemoEntity) {
        // 알림 클릭하면 앱이 열리게 하는 '인텐트' 준비
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 꾸미기
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘 (기본 안드로이드 아이콘)
            .setContentTitle(null) // 제목 없음
            .setContentText(memo.content) // 내용 넣기
            .setPriority(NotificationCompat.PRIORITY_LOW) // 소리 안 나게
            .setContentIntent(pendingIntent) // 클릭 시 앱 열기
            .setOngoing(true) // 사용자가 스와이프해서 못 지우게 함 (Sticky)

        // 알림 설정
        notificationManager.notify(memo.id.toInt(), builder.build())
    }

    // 알림 지우기
    fun cancelNotification(memo: MemoEntity) {
        notificationManager.cancel(memo.id.toInt())
    }
}