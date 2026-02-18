package com.example.notisticky.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notisticky.data.local.MemoEntity
import com.example.notisticky.data.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MemoRepository
) : ViewModel() {

    // 데이터 스트림 (DB -> ViewModel -> UI)
    // stateIn: Flow를 UI가 구독하기 좋은 StateFlow로 바꿈
    val memoList = repository.memos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 화면 꺼져도 5초간 유지 (회전 시 리로드 방지)
        initialValue = emptyList() // 초기값
    )

    // 토글 이벤트 처리
    fun onToggle(memo: MemoEntity) {
        viewModelScope.launch {
            repository.toggleNotification(memo)
        }
    }

    // 삭제 이벤트 처리
    fun onDelete(memo: MemoEntity) {
        viewModelScope.launch {
            repository.delete(memo)
        }
    }
}