package com.example.notisticky.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notisticky.data.local.MemoDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDismissReceiver : BroadcastReceiver() {

    @Inject
    lateinit var memoDao: MemoDao

    override fun onReceive(context: Context, intent: Intent) {
        val memoId = intent.getLongExtra("MEMO_ID", -1L)

        if (memoId != -1L) {
            // 시스템에게 리시버 비동기 작업 동안 살리라고 알림
            val pendingResult = goAsync()

            // 안전한 코루틴 스코프에서 백그라운드 작업 실행
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                try {
                    // DB 업데이트 (끄기)
                    memoDao.turnOffMemo(memoId)
                } finally {
                    //작업이 끝나면 리시버 종료해도 된다고 알림
                    pendingResult.finish()
                }
            }
        }
    }
}