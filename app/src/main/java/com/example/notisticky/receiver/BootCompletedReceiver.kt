package com.example.notisticky.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notisticky.data.local.MemoDao
import com.example.notisticky.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var memoDao: MemoDao

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            //시스템에 비동기 작업이 있음을 알림
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // DB에서 스위치가 켜진 메모들을 싹 다 가져온다
                    val postedMemos = memoDao.getPostedMemos()

                    // 하나씩 꺼내서 상단바에 다시 띄워준다
                    postedMemos.forEach { memo ->
                        notificationHelper.showNotification(memo)
                    }
                } finally {
                    // 비동기 작업이 모두 끝났음을 알림
                    pendingResult.finish()
                }
            }
        }
    }
}