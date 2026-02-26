package com.sungs.notisticky.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sungs.notisticky.data.local.MemoDao
import com.sungs.notisticky.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var memoDao: MemoDao

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val memoId = intent.getLongExtra("MEMO_ID", -1L)

        if (memoId != -1L) {
            // 비동기 작업 시작 알림 (프로세스 유지)
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // DB에서 해당 메모를 찾는다
                    val memo = memoDao.getMemoById(memoId)

                    // 메모 다시 살리기
                    if (memo != null && memo.isPosted) {
                        notificationHelper.showNotification(memo)
                    }
                } finally {
                    // 비동기 작업 종료 알림
                    pendingResult.finish()
                }
            }
        }
    }
}