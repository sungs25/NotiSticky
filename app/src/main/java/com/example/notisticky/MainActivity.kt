package com.example.notisticky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notisticky.ui.add.MemoAddScreen
import com.example.notisticky.ui.home.HomeScreen
import com.example.notisticky.ui.theme.NotiStickyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint //Hilt 시작점
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 테마 설정ㄴ
            NotiStickyTheme {
                // 운전대 생성
                val navController = rememberNavController()

                // 라우터 설정
                NavHost(navController = navController, startDestination = "home") {

                    // URL 1: home
                    composable("home") {
                        HomeScreen() {
                            navController.navigate("add")
                        }
                    }

                    // URL 2: add
                    composable("add") {
                        MemoAddScreen() {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

