package com.example.notisticky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notisticky.ui.home.HomeScreen
import com.example.notisticky.ui.theme.NotiStickyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // 1. Hilt 시작점
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 테마 설정 (기본 생성된 코드 있으면 그거 쓰시고, 없으면 생략 가능)
            NotiStickyTheme {
                // 2. 운전대 생성
                val navController = rememberNavController()

                // 3. 라우터 설정 (여기서 길을 안내합니다)
                NavHost(navController = navController, startDestination = "home") {

                    // URL 1: "home"
                    composable("home") {
                        HomeScreen() {
                            navController.navigate("add")
                        }
                    }

                    // URL 2: "add"
                    composable("add") {
                        Text("여기는 메모 작성 화면")
                    }
                }
            }
        }
    }
}

