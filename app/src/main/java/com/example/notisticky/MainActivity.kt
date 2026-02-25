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
import com.example.notisticky.ui.onboarding.OnboardingScreen
import com.example.notisticky.util.AdManager
import com.google.android.gms.ads.MobileAds
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {
            AdManager.loadAd(this) // 전면 광고 미리 불러오기
        }

        setContent {
            NotiStickyTheme {

                val mainViewModel: MainViewModel = hiltViewModel()

                if (mainViewModel.isLoading.value) {
                    // DataStore에서 값을 읽어오는 0.1초 동안 텅 빈 하얀 화면을 그려서 대기합니다.
                } else {
                    // 로딩이 끝났다면 본격적으로 앱 화면을 그리기


                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = mainViewModel.startDestination.value
                    ) {

                        // 온보딩 화면 추가
                        composable("onboarding") {
                            OnboardingScreen(
                                onFinish = {
                                    mainViewModel.finishOnboarding()

                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Home 화면
                        composable("home") {
                            HomeScreen(
                                onAddClick = { navController.navigate("add") },
                                onMemoClick = { memoId ->
                                    navController.navigate("add?memoId=$memoId")
                                }
                            )
                        }

                        // Add 화면
                        composable(
                            route = "add?memoId={memoId}",
                            arguments = listOf(
                                navArgument("memoId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
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
                } // else 닫기
            }
        }
    }
}
