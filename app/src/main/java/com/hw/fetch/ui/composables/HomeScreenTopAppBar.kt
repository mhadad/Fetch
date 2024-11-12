package com.hw.fetch.ui.composables

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable()
fun HomeScreenTopAppBar(isGroupByListIdFilterSelected: Boolean, isNotNullFilterSelected: Boolean,filterNotNullCallback: ()-> Unit, groupByFilterCallback: () -> Unit, clearGroupByFilterCallback: ()-> Unit, clearFilterNotNullCallback: () -> Unit) {
    var wasGroupBySelected by rememberSaveable { mutableStateOf(isGroupByListIdFilterSelected) }
    var wasNotNullFilterSelected by rememberSaveable { mutableStateOf(isNotNullFilterSelected) }

    var selectedFilterIndex by rememberSaveable { mutableStateOf(0) } // Name not null filter is 1 and Group by filter is 2
    FlowRow (modifier = Modifier.padding(8.dp)){
        FilterChip(
            onClick = {
                wasGroupBySelected = !wasGroupBySelected
                wasNotNullFilterSelected = false
                if(wasGroupBySelected && selectedFilterIndex != 2)
                    selectedFilterIndex = 2 // 2 is button index
                else // ie. the user deselects all filters
                    selectedFilterIndex = 0 // 0 button index means no filters were selected
                if(wasGroupBySelected == true && selectedFilterIndex == 2)
                    groupByFilterCallback()
                else if(!wasGroupBySelected && selectedFilterIndex == 0)
                    clearGroupByFilterCallback()
                      },
            content = { Text("Group by listId") },
            selected = 2 == selectedFilterIndex,
            modifier = Modifier.testTag("groupByListIdFilter")
        )
        FilterChip(
            onClick = {
                wasNotNullFilterSelected = !wasNotNullFilterSelected
                wasGroupBySelected = false
                if(wasNotNullFilterSelected && selectedFilterIndex != 1)
                    selectedFilterIndex = 1 // 1 is button index
                else // ie. the user deselects all filters
                    selectedFilterIndex = 0 // 0 button index means no filters were selected
                if(wasNotNullFilterSelected == true && selectedFilterIndex == 1){
                    filterNotNullCallback()
                }
                else if(!wasNotNullFilterSelected && selectedFilterIndex == 0)
                    clearFilterNotNullCallback()
                      },
            content = { Text("Filter by name") },
            selected = 1 == selectedFilterIndex,
            modifier = Modifier.testTag("nameNotNullFilter")
        )
    }
}