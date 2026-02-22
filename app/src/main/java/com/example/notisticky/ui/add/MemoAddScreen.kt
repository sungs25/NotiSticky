package com.example.notisticky.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoAddScreen(
    viewModel: MemoAddViewModel = hiltViewModel(),
    // 뒤로가기 함수 (저장 완료 후 or 뒤로가기 버튼 클릭 시)
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                // 모드에 따라 타이틀 변경
                title = { Text(if (viewModel.isEditMode) "메모 수정" else "새 메모") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                // 우측 상단 액션 버튼 영역 (휴지통)
                actions = {
                    if (viewModel.isEditMode) { // 수정 모드일 때만 휴지통 보이기
                        IconButton(
                            onClick = { viewModel.deleteMemo(onDeleted = onBack) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "삭제",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = viewModel.content.value,
                onValueChange = { viewModel.updateContent(it) },
                placeholder = { Text("내용을 입력하세요") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.saveMemo(onSaved = onBack) }
            ) {
                Text(if (viewModel.isEditMode) "수정하기" else "저장하기")
            }
        }
    }
}