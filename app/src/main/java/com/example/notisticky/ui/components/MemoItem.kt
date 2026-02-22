package com.example.notisticky.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.notisticky.data.local.MemoEntity

@Composable
fun MemoItem(
    memo: MemoEntity,
    onToggle: (MemoEntity) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 내용 보여주기
                Text(
                    text = memo.content,
                    style = MaterialTheme.typography.bodyLarge, // 글자 크기 키움
                    maxLines = 1, // 너무 길면 한 줄까지만
                    overflow = TextOverflow.Ellipsis // 말줄임표(...) 처리
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = memo.isPosted,
                onCheckedChange = { onToggle(memo) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "상세 보기",
                tint = Color.Gray
            )
        }
    }
}