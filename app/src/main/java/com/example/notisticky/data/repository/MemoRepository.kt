package com.example.notisticky.data.repository

import com.example.notisticky.data.local.MemoDao
import com.example.notisticky.data.local.MemoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemoRepository @Inject constructor(
    private val memoDao: MemoDao
) {
    // 1. 전체 조회 (실시간)
    val memos: Flow<List<MemoEntity>> = memoDao.getAllMemos()

    // 2. 추가 및 수정 (내용 변경할 때)
    suspend fun insert(memo: MemoEntity) {
        memoDao.insertMemo(memo)
    }

    // 3. 삭제
    suspend fun delete(memo: MemoEntity) {
        memoDao.deleteMemo(memo)
    }

    // 4. 스위치 토글 (리스트에서 바로 껐다 켜기용)
    suspend fun toggleNotification(memo: MemoEntity) {
        val updatedMemo = memo.copy(isPosted = !memo.isPosted) // 상태 반전
        memoDao.insertMemo(updatedMemo) // 저장
    }
}