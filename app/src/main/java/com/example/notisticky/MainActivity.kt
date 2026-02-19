package com.example.notisticky

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notisticky.ui.add.MemoAddScreen
import com.example.notisticky.ui.home.HomeScreen
import com.example.notisticky.ui.theme.NotiStickyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiStickyTheme {


                // 권한 요청 런처 만들기
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        // 허락받았을 때/거절당했을 때 로직 (일단 비워둠)
                    }
                )

                // 앱 켜질 때 딱 한 번 권한 요청 실행
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }


                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen() {
                            navController.navigate("add")
                        }
                    }
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
