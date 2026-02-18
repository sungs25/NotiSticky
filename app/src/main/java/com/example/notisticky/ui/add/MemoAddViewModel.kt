package com.example.notisticky.ui.add

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notisticky.data.local.MemoEntity
import com.example.notisticky.data.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoAddViewModel @Inject constructor(
    private val repository: MemoRepository
) : ViewModel() {

    // 입력받을 텍스트 상태
    var content = mutableStateOf("")
        private set

    // 텍스트 변경 이벤트 처리
    fun updateContent(newText: String) {
        content.value = newText
    }

    // 저장 기능
    fun saveMemo(onSaved: () -> Unit) {
        val text = content.value
        if (text.isBlank()) return

        viewModelScope.launch {
            val memo = MemoEntity(id = 0, content = text)
            repository.insert(memo)
            onSaved()
        }
    }
}