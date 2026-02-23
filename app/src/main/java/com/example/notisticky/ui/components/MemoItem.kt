package com.example.notisticky.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle // 체크된 상태
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notisticky.data.local.MemoEntity
import com.example.notisticky.ui.theme.Pretendard

@Composable
fun MemoItem(
    memo: MemoEntity,
    onToggle: (MemoEntity) -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 커스텀 체크박스 아이콘
            IconButton(
                onClick = { onToggle(memo) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (memo.isPosted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = if (memo.isPosted) "고정 해제" else "상단바 고정",
                    tint = if (memo.isPosted) Color(0xFF333333) else Color.LightGray,
                    modifier = Modifier.fillMaxSize() //
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // 체크박스와 글자 사이 간격

            // 폰트 적용 및 디자인 다듬기
            Text(
                text = memo.content,
                fontFamily = Pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = if (memo.isPosted) Color(0xFF333333) else Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

        }

        // 아주 얇고 연한 구분선
        HorizontalDivider(
            color = Color.LightGray.copy(alpha = 0.15f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}