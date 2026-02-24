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
import android.net.Uri
import androidx.core.net.toUri

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
        // 딥링크(특정 목적지)를 향하는 인텐트 생성
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "notisticky://memo/${memo.id}".toUri(), /* 고유 주소 */
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // 알림마다 고유 티켓 번호 부여
        val pendingIntent = PendingIntent.getActivity(
            context,
            memo.id.toInt(),
            deepLinkIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 알림 삭제(스와이프) 시 실행될 인텐트
        val dismissIntent = Intent(context, com.example.notisticky.receiver.NotificationDismissReceiver::class.java).apply {
            putExtra("MEMO_ID", memo.id) // 어떤 메모가 지워졌는지 ID를 담아둠
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, memo.id.toInt(), dismissIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val unpinIntent = Intent(context, com.example.notisticky.receiver.UnpinReceiver::class.java).apply {
            putExtra("MEMO_ID", memo.id)
        }
        val unpinPendingIntent = PendingIntent.getBroadcast(
            context,
            memo.id.toInt(),
            unpinIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 알림 꾸미기 (builder)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(null)
            .setContentText(memo.content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setDeleteIntent(dismissPendingIntent)
            // 🌟 [수정] 액션 버튼을 '고정 해제'로 변경!
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel, // 기본 X 모양(닫기) 아이콘
                "고정 해제", // 텍스트 변경
                unpinPendingIntent
            )

        notificationManager.notify(memo.id.toInt(), builder.build())
    }

    // 알림 지우기
    fun cancelNotification(memo: MemoEntity) {
        notificationManager.cancel(memo.id.toInt())
    }
}