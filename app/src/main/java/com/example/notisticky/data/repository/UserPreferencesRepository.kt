package com.example.notisticky.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore 인스턴스 생성 (이름은 "settings"로 지정)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 저장할 데이터의 열쇠 이름 설정
    private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")

    // 처음 켰는지 확인 (값이 없으면 기본값 true)
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) } // 에러 나면 빈 값으로 처리
        .map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }

    // 온보딩 끝났다고 기록하기 (false로 변경)
    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }
}