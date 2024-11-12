package com.hw.fetch.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hw.fetch.data.models.FetchHiringAssessmentModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable()
fun HomeScreenExpandableList(
    items: Map<Int,List<FetchHiringAssessmentModel>>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val pullToRefreshBoxState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshBoxState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        LazyColumn(
            Modifier.fillMaxSize().testTag("homeScreenExpandableList"),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            state = lazyListState
        ) {
            items.forEach { (listId, itemsGroupedByListId) ->
                // Header for each group
                item {
                    Text(
                        text = "List ID: $listId",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Display each item in the group
                items(itemsGroupedByListId, key = {item -> item.id}) { fetchHiringAssessmentModelObj ->
                    ListItem(modifier = Modifier.drawBehind { drawRect(Color.Cyan) },
                        leadingContent = { Text(text = "Item ID: ${fetchHiringAssessmentModelObj.id}") },
                        headlineContent = {Text("Name: ${fetchHiringAssessmentModelObj.name}")},
                        trailingContent = {Text("ListID: ${fetchHiringAssessmentModelObj.listId}")},
                        tonalElevation = 10.dp,
                        shadowElevation = 5.dp
                    )
                }
            }
        }
    }
}