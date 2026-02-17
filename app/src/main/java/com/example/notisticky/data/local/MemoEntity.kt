package com.example.notisticky.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memos") // 보통 테이블명은 소문자 복수형을 많이 씁니다 (선택사항)
data class MemoEntity( // private 제거!
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 초기값 0 설정 (새 글 작성 시 편함), var -> val 권장(ID는 안 바뀌니까)

    @ColumnInfo(name = "content")
    var content: String,

    @ColumnInfo(name = "isPosted")
    var isPosted: Boolean = false,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = System.currentTimeMillis() // 생성 시간 자동 입력
)