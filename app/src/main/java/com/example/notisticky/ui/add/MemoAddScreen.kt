package com.example.notisticky.ui.add

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notisticky.ui.theme.Pretendard
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoAddScreen(
    viewModel: MemoAddViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 텍스트 창에 포커스를 줄 요청기
    val focusRequester = remember { FocusRequester() }

    // 화면이 처음 열릴 때 딱 한 번, 커서를 꽂아달라고 요청
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.content.value.isNotBlank()) {
                            viewModel.saveMemo(onSaved = onBack)
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color(0xFF333333)
                        )
                    }
                },
                title = { },
                actions = {
                    if (viewModel.isEditMode) {
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "삭제",
                                tint = Color(0xFF757575)
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            BasicTextField(
                value = viewModel.content.value,
                onValueChange = { viewModel.updateContent(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = Pretendard,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                    color = Color(0xFF333333)
                ),
                cursorBrush = SolidColor(Color(0xFF333333)),
                decorationBox = { innerTextField ->
                    if (viewModel.content.value.isEmpty()) {
                        Text(
                            text = "메모를 작성하세요",
                            color = Color(0xFFBDBDBD),
                            fontSize = 18.sp,
                            fontFamily = Pretendard
                        )
                    }
                    // 실제 텍스트가 그려지는 부분
                    innerTextField()
                }
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