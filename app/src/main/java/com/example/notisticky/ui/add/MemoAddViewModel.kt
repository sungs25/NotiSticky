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

    val isEditMode: Boolean
        get() = currentMemoId.value != 0L

    init {
        val memoId = savedStateHandle.get<Long>("memoId") ?: -1L
        if (memoId != -1L) {
            viewModelScope.launch {
                repository.getMemoById(memoId)?.let { existingMemo ->
                    currentMemoId.value = existingMemo.id
                    content.value = existingMemo.content
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
            val memo = MemoEntity(id = currentMemoId.value, content = text)
            repository.insert(memo)
            onSaved()
        }
    }

    fun deleteMemo(onDeleted: () -> Unit) {
        if (currentMemoId.value == 0L) return

        viewModelScope.launch {
            val memoToDelete = MemoEntity(id = currentMemoId.value, content = content.value)
            repository.delete(memoToDelete)
            onDeleted()
        }
    }
}