package com.sungs.notisticky.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sungs.notisticky.data.local.MemoEntity
import com.sungs.notisticky.data.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MemoRepository
) : ViewModel() {

    // 데이터 스트림은 Room과 StateFlow가 자체적으로 안전하게 관리하므로
    // try-catch나 락이 필요 없습니다. (아주 잘 짜인 코드입니다!)
    val memoList = repository.memos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // [추가] 작업 진행 상태 (스위치 연타 등 중복 실행 방지)
    var isProcessing = mutableStateOf(false)
        private set

    // 토글 이벤트 처리
    fun onToggle(memo: MemoEntity) {
        // 이미 다른 작업을 처리 중이면 스위치 연타 무시
        if (isProcessing.value) return

        viewModelScope.launch {
            isProcessing.value = true
            try {
                repository.toggleNotification(memo)
            } catch (e: Exception) {
                // DB 업데이트나 알림 띄우기 실패 시 앱 죽음 방지
                e.printStackTrace()
            } finally {
                isProcessing.value = false
            }
        }
    }

    // 삭제 이벤트 처리
    fun onDelete(memo: MemoEntity) {
        // 삭제 연타 무시
        if (isProcessing.value) return

        viewModelScope.launch {
            isProcessing.value = true
            try {
                repository.delete(memo)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isProcessing.value = false
            }
        }
    }
}