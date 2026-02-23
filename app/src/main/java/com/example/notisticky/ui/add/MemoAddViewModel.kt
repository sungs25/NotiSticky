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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var content = mutableStateOf("")
        private set

    private var currentMemoId = mutableStateOf(0L)

    // [핵심 1] 작업 진행 상태를 나타내는 변수 (중복 실행 방지용 Lock 역할)
    var isLoading = mutableStateOf(false)
        private set

    val isEditMode: Boolean
        get() = currentMemoId.value != 0L

    init {
        val memoId = savedStateHandle.get<Long>("memoId") ?: -1L
        if (memoId != -1L) {
            viewModelScope.launch {
                if (isLoading.value) return@launch // 이미 불러오는 중이면 무시

                isLoading.value = true
                try {
                    repository.getMemoById(memoId)?.let { existingMemo ->
                        currentMemoId.value = existingMemo.id
                        content.value = existingMemo.content
                    }
                } catch (e: Exception) {
                    // DB 읽기 실패 시 안전하게 에러 로그만 남김 (앱 안 죽음)
                    e.printStackTrace()
                } finally {
                    isLoading.value = false // 성공하든 실패하든 무조건 락 해제
                }
            }
        }
    }

    fun updateContent(newText: String) {
        content.value = newText
    }

    fun saveMemo(onSaved: () -> Unit) {
        val text = content.value

        // [핵심 2] 빈 내용이거나, 이미 저장 중(isLoading == true)이면 실행 안 함 (따닥 방지)
        if (text.isBlank() || isLoading.value) return

        viewModelScope.launch {
            isLoading.value = true
            try {
                val memo = MemoEntity(id = currentMemoId.value, content = text)
                repository.insert(memo)
                onSaved() // 정상 저장 시 화면 닫기
            } catch (e: Exception) {
                // 저장 실패 시 처리 (Toast 띄워주면 더 좋음)
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteMemo(onDeleted: () -> Unit) {
        if (currentMemoId.value == 0L || isLoading.value) return

        viewModelScope.launch {
            isLoading.value = true
            try {
                val memoToDelete = MemoEntity(id = currentMemoId.value, content = content.value)
                repository.delete(memoToDelete)
                onDeleted() // 정상 삭제 시 화면 닫기
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
}