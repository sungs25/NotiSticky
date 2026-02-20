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
class NotificationDismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var memoDao: MemoDao

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val memoId = intent.getLongExtra("MEMO_ID", -1L)

        if (memoId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                // DB에서 해당 메모를 찾는다
                val memo = memoDao.getMemoById(memoId)

                // 메모 다시 살리기
                if (memo != null && memo.isPosted) {
                    notificationHelper.showNotification(memo)
                }
            }
        }
    }
}