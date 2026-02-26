package com.sungs.notisticky.ui.onboarding

import com.sungs.notisticky.R
import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sungs.notisticky.service.MemoTileService
import kotlinx.coroutines.launch
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

// 온보딩 내용 데이터
data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val showTileButton: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "중요한 일, 잊지 마세요",
            description = "메모를 상단바에 찰싹 고정해두고\n폰을 켤 때마다 확인하세요.",
            icon = Icons.Rounded.Notifications
        ),
        OnboardingPage(
            title = "상단바에서 1초 만에",
            // 안드로이드 버전에 따라 텍스트 분기 처리
            description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                "아래 버튼을 눌러 타일을 추가해 보세요.\n앱을 켤 필요 없이 즉시 작성할 수 있습니다."
            } else {
                "상단바를 내려 편집 버튼을 누르고\n'새 메모 작성' 타일을 추가해보세요.\n앱을 켤 필요 없이 즉시 작성할 수 있습니다."
            },
            icon = Icons.Rounded.Edit,
            showTileButton = true // 2페이지에서만 버튼 활성화 플래그 ON!
        ),
        OnboardingPage(
            title = "준비 완료!",
            description = "모든 준비가 끝났습니다.\n첫 메모를 작성해보세요!",
            icon = Icons.Rounded.CheckCircle
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // 권한을 허용하든 거절하든, 팝업이 닫히면 무조건 홈 화면으로 보냅니다.
            onFinish()
        }
    )

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { position ->
                // 현재 페이지 데이터를 넘겨줌
                OnboardingPageContent(page = pages[position])
            }

            // 하단 인디케이터 및 다음/시작 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index) Color(0xFF333333) else Color.LightGray
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // 마지막 3페이지에서 '시작하기' 버튼을 눌렀을 때
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                //  팝업을 띄우기 전에, 이미 권한이 있는지 먼저 검사
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    // 이미 권한이 있다면 바로 홈으로
                                    onFinish()
                                } else {
                                    // 권한이 없을 때만 런처를 실행해서 팝업 요청
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                // 안드로이드 12 이하는 프리패스
                                onFinish()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) "시작하기" else "다음",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        // 안드로이드 13 이상이고, showTileButton이 true일 때만 '타일 추가 버튼' 노출
        if (page.showTileButton && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val statusBarManager = context.getSystemService(StatusBarManager::class.java)
                    val componentName = ComponentName(context, MemoTileService::class.java)
                    statusBarManager.requestAddTileService(
                        componentName,
                        "새 메모 작성", // 팝업에 보일 타일 이름
                        Icon.createWithResource(context, R.drawable.ic_notification),
                        context.mainExecutor,
                        { result -> }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF5F5F5),
                    contentColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("퀵타일 추가하기", fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
            }
        }
    }
}