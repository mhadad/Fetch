package com.hw.fetch.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.hw.fetch.data.models.FetchHiringAssessmentModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable()
fun  HomeScreenList(
    items: List<FetchHiringAssessmentModel>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val pullToRefreshBoxState = rememberPullToRefreshState()

        PullToRefreshBox(
            modifier = Modifier.testTag("homeScreenList"),
            state = pullToRefreshBoxState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,

        ) {
            LazyColumn(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                state = lazyListState
            ) {
                items(items, key = {item -> item.id}) { item ->
                    ListItem(modifier = Modifier.drawBehind { drawRect(Color.Cyan) },
                        leadingContent = { Text(text = "Item ID: ${item.id}") },
                        headlineContent = {Text("Name: ${item.name}")},
                        trailingContent = {Text("ListID: ${item.listId}")},
                        tonalElevation = 10.dp,
                        shadowElevation = 5.dp
                    )
                }
            }
        }
}