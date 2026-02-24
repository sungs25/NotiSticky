package com.example.notisticky.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notisticky.data.repository.MemoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UnpinReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: MemoRepository

    override fun onReceive(context: Context, intent: Intent) {
        val memoId = intent.getLongExtra("MEMO_ID", -1L)

        if (memoId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                val memo = repository.getMemoById(memoId)

                if (memo != null && memo.isPosted) {
                    // DB를 false로 바꾸고, 알림도 끔.
                    repository.toggleNotification(memo)
                }
            }
        }
    }
}