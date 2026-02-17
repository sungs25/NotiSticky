package com.example.notisticky.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MemoEntity::class], // 테이블 등록
    version = 1, // DB 버전 (나중에 테이블 구조 바꾸면 이거 올려야 함)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()  {//Room 구현
    abstract fun memoDao(): MemoDao //DAO를 꺼내주는 함수
}