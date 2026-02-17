package com.example.notisticky.di

import android.content.Context
import androidx.room.Room
import com.example.notisticky.data.local.AppDatabase
import com.example.notisticky.data.local.MemoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,//권한 설정
            AppDatabase::class.java,
            "noti_sticky_db" // DB 파일 이름
        ).build()
    }

    @Provides
    @Singleton
    fun provideMemoDao(database: AppDatabase): MemoDao {
        return database.memoDao() // 여기서 DAO를 꺼낸다
    }
}