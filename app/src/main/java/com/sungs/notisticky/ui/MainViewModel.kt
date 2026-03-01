package com.sungs.notisticky

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sungs.notisticky.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // 로딩 상태
    val isLoading = mutableStateOf(true)

    // 시작 화면 결정
    val startDestination = mutableStateOf("onboarding")

    init {
        viewModelScope.launch {
            val isFirst = userPreferencesRepository.isFirstLaunch.first()
            startDestination.value = if (isFirst) "onboarding" else "home"
            isLoading.value = false
        }
    }

    // 온보딩 '시작하기' 버튼을 눌렀을 때 호출할 함수
    fun finishOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted()
        }
    }
}