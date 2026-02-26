package com.sungs.notisticky.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import com.sungs.notisticky.MainActivity
import androidx.core.net.toUri

class MemoTileService : TileService() {

    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()

        //딥링크 목적지 설정
        val intent = Intent(
            Intent.ACTION_VIEW,
            "notisticky://memo/-1".toUri(),
            this,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // 버전별 분기 처리
        if (Build.VERSION.SDK_INT >= 34) {
            // 안드로이드 14 이상
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            // 안드로이드 13 이하\
            startActivityAndCollapse(intent)
        }
    }
}