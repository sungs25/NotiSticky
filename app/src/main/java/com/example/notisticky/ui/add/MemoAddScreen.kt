package com.example.notisticky.ui.add

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notisticky.ui.theme.Pretendard



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoAddScreen(
    viewModel: MemoAddViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 스마트폰의 물리적 '뒤로 가기' 제스처를 할 때 자동 저장
    BackHandler {
        if (viewModel.content.value.isNotBlank()) {
            viewModel.saveMemo(onSaved = onBack)
        } else {
            onBack() // 내용이 없으면 그냥 뒤로 가기
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // 제목 비우기
                navigationIcon = {
                    // 화면 상단의 '뒤로 가기 화살표'를 누를 때도 자동 저장
                    IconButton(onClick = {
                        if (viewModel.content.value.isNotBlank()) {
                            viewModel.saveMemo(onSaved = onBack)
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "저장 및 뒤로가기",
                            tint = Color(0xFF333333)
                        )
                    }
                },
                actions = {
                    // 수정 모드일 때만 휴지통 보이기
                    if (viewModel.isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "삭제",
                                tint = Color.LightGray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 화면 전체를 차지하는 테두리 없는 텍스트 에디터
            TextField(
                modifier = Modifier
                    .fillMaxSize() // 남은 화면 꽉 채우기
                    .padding(horizontal = 24.dp),
                value = viewModel.content.value,
                onValueChange = { viewModel.updateContent(it) },
                placeholder = {
                    Text(
                        "메모를 작성하세요",
                        color = Color.LightGray,
                        fontFamily = Pretendard
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent, // 밑줄 완벽 제거
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF333333)
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = Pretendard,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    color = Color(0xFF333333)
                )
            )
        }
    }

    // 팝업창은 기존과 동일
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("메모 삭제",  fontFamily = Pretendard,  fontWeight = FontWeight.Bold) },
            text = { Text("이 메모를 정말 삭제하시겠습니까?", fontFamily = Pretendard ) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteMemo(onDeleted = onBack)
                }) {
                    Text("삭제", color = Color(0xFFE53935),  fontFamily = Pretendard )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소", color = Color.Gray, fontFamily = Pretendard )
                }
            }
        )
    }
}