package com.example.notisticky.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notisticky.ui.components.MemoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),

    onAddClick: () -> Unit
) {
    //데이터 구독 (StateFlow -> State)
    val memos by viewModel.memoList.collectAsState()

    Scaffold(
        // 상단바
        topBar = {
            TopAppBar(title = { Text("NotiSticky") })
        },
        // 우측 하단 + 버튼
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "메모 추가")
            }
        }
    ) { innerPadding ->
        //리스트 그리기 (RecyclerView 대체)
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            // memos 리스트를 반복해서 그림
            items(
                items = memos,
                key = { memo -> memo.id } // ID로 구분
            ) { memo ->
                MemoItem(
                    memo = memo,
                    onToggle = { clickedMemo ->
                        viewModel.onToggle(clickedMemo)
                    }
                )
            }
        }
    }
}