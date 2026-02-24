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
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.navDeepLink

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

                    //Home 화면 (아이템 클릭 시 해당 ID를 들고 add로 이동)
                    composable("home") {
                        HomeScreen(
                            onAddClick = { navController.navigate("add") }, // 플러스 버튼
                            onMemoClick = { memoId ->
                                navController.navigate("add?memoId=$memoId") // 메모 클릭
                            }
                        )
                    }

                    // Add 화면 (memoId 파라미터 받기)
                    composable(
                        route = "add?memoId={memoId}",
                        arguments = listOf(
                            navArgument("memoId") {
                                type = NavType.LongType
                                defaultValue = -1L // 안 넘겨주면 -1 (새 메모)
                            }
                        ),
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "notisticky://memo/{memoId}" }
                        )
                    ) {
                        MemoAddScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
