package com.example.notisticky.ui.add

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notisticky.data.local.MemoEntity
import com.example.notisticky.data.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoAddViewModel @Inject constructor(
    private val repository: MemoRepository,
    savedStateHandle: SavedStateHandle // 네비게이션 파라미터를 받는 객체
) : ViewModel() {

    var content = mutableStateOf("")
        private set

    // 현재 작업 중인 메모의 ID (기본값 0L = 새 메모)
    private var currentMemoId: Long = 0L

    init {
        val memoId = savedStateHandle.get<Long>("memoId") ?: -1L
        if (memoId != -1L) {
            // 넘어온 ID가 있다면 수정
            viewModelScope.launch {
                repository.getMemoById(memoId)?.let { existingMemo ->
                    currentMemoId = existingMemo.id
                    content.value = existingMemo.content // 화면에 기존 내용 채워넣기
                }
            }
        }
    }

    fun updateContent(newText: String) {
        content.value = newText
    }

    fun saveMemo(onSaved: () -> Unit) {
        val text = content.value
        if (text.isBlank()) return

        viewModelScope.launch {
            // id = 0이면 새 메모 생성, 기존 ID면 덮어쓰기 됨
            val memo = MemoEntity(id = currentMemoId, content = text)
            repository.insert(memo)
            onSaved()
        }
    }
}