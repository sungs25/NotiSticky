package com.example.notisticky.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
                title = { Text("메모 작성") },
                navigationIcon = {
                    // 뒤로가기 화살표 버튼
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
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
            // 입력창
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 남은 공간 꽉 채우기

                value = viewModel.content.value,

                onValueChange = { viewModel.updateContent(it) },

                placeholder = { Text("내용을 입력하세요") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 저장 버튼
            Button(
                modifier = Modifier.fillMaxWidth(),

                onClick = { viewModel.saveMemo(onSaved = onBack) }
            ) {
                Text("저장하기")
            }
        }
    }
}