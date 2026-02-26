package com.sungs.notisticky.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sungs.notisticky.ui.components.AdmobBanner
import com.sungs.notisticky.ui.components.MemoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddClick: () -> Unit,
    onMemoClick: (Long) -> Unit
) {
    // DB 데이터 구독
    val memos by viewModel.memoList.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { /* ... */ },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                modifier = Modifier.padding(16.dp),
                containerColor = Color(0xFF333333)
            ) {
                Icon(Icons.Filled.Add, "추가", tint = Color.White)
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
            ) {
                AdmobBanner()
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (memos.isEmpty()) {
                Text(
                    text = "메모를 기록해 보세요.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = memos, key = { it.id }) { memo ->
                        MemoItem(
                            memo = memo,
                            onToggle = { viewModel.onToggle(it) },
                            onClick = { onMemoClick(memo.id) }
                        )
                    }
                }
            }
        }
    }
}