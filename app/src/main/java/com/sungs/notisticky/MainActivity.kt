package com.sungs.notisticky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // 🌟 [추가] 뷰모델을 액티비티 레벨에서 쓰기 위해 필요
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.sungs.notisticky.ui.add.MemoAddScreen
import com.sungs.notisticky.ui.home.HomeScreen
import com.sungs.notisticky.ui.onboarding.OnboardingScreen
import com.sungs.notisticky.ui.theme.NotiStickyTheme
import com.sungs.notisticky.util.AdManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isLoading.value
        }

        MobileAds.initialize(this) {
            AdManager.loadAd(this)
        }

        setContent {
            NotiStickyTheme {
                if (!mainViewModel.isLoading.value) {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = mainViewModel.startDestination.value
                    ) {

                        // 온보딩 화면
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
                }
            }
        }
    }
}