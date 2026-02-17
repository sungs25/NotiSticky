package com.example.notisticky.data.local

import androidx.room.Database
import androidx.room.RoomDatabase // 이게 빠져서 빨간 줄 떴을 수도 있습니다.

@Database(
    entities = [MemoEntity::class], // 테이블 등록
    version = 1, // DB 버전 (나중에 테이블 구조 바꾸면 이거 올려야 함)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() { // [수정] extends -> : RoomDatabase()
    abstract fun memoDao(): MemoDao // [역할] DAO를 꺼내주는 함수
}