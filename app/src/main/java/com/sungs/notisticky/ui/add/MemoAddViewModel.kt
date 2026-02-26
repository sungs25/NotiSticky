package com.sungs.notisticky.ui.add

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sungs.notisticky.data.local.MemoEntity
import com.sungs.notisticky.data.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoAddViewModel @Inject constructor(
    private val repository: MemoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val content = mutableStateOf("")

    private var currentMemoId = mutableStateOf(0L)

    // 작업 진행 상태를 나타내는 변수 (중복 실행 방지용 Lock 역할)
    val isLoading = mutableStateOf(false)

    // DB에서 처음 불러온 '원본' 내용을 기억해 둘 변수
    private var originalContent = ""

    val isEditMode: Boolean
        get() = currentMemoId.value != 0L

    // 현재 내용이 원본과 다른지(수정되었는지) 확인하는 플래그
    val isModified: Boolean
        get() = content.value != originalContent

    init {
        val memoId = savedStateHandle.get<Long>("memoId") ?: -1L
        if (memoId != -1L) {
            viewModelScope.launch {
                if (isLoading.value) return@launch

                isLoading.value = true
                try {
                    repository.getMemoById(memoId)?.let { existingMemo ->
                        currentMemoId.value = existingMemo.id
                        content.value = existingMemo.content


                        originalContent = existingMemo.content
                    }
                } catch (e: Exception) {
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

        if (text.isBlank() || isLoading.value || !isModified) {
            onSaved() // 아무것도 안 하고 그냥 성공한 척 화면만 닫게 넘겨버림
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val memo = MemoEntity(id = currentMemoId.value, content = text)
                repository.insert(memo)

                originalContent = text

                onSaved() // 정상 저장 시 화면 닫기
            } catch (e: Exception) {
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