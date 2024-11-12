package com.hw.fetch.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.hilt.navigation.compose.hiltViewModel
import com.hw.fetch.data.models.UiStatesModel
import com.hw.fetch.domain.view_models.HomeScreenViewModel
import com.hw.fetch.ui.composables.HomeScreenExpandableList
import com.hw.fetch.ui.composables.HomeScreenList
import com.hw.fetch.ui.composables.HomeScreenTopAppBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen( homeScreenViewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>()) {
    val homeScreenData = homeScreenViewModel.homeScreenData.observeAsState()
    val homeScreenGroupedData = homeScreenViewModel.homeScreenDataGrouped.observeAsState()
    var isHomeScreenListRefreshing by remember { mutableStateOf(false) }
    var isHomeScreenExpandableListRefreshing by remember { mutableStateOf(false) }
    var isNameNotNullFilterSelected by remember { mutableStateOf(false) }
    var isGroupByListIdFilterSelected by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        homeScreenViewModel.loadHomeScreenData()
    }
    LaunchedEffect( isHomeScreenListRefreshing, isHomeScreenExpandableListRefreshing) {
        if(isHomeScreenListRefreshing || isHomeScreenExpandableListRefreshing){
            homeScreenViewModel.clearGroupByFilter()
            homeScreenViewModel.loadHomeScreenData()
            isHomeScreenListRefreshing = false
            isHomeScreenExpandableListRefreshing = false
        }
    }

    LaunchedEffect(isNameNotNullFilterSelected) {
        // User selects name not null nor blank filter
        if (isNameNotNullFilterSelected && !isHomeScreenListRefreshing) {
            homeScreenViewModel.clearGroupByFilter() // clear the group by filter selection since we need to load the expandable list composable
            homeScreenViewModel.filterNameNotNull_Blank()
        } else if(!isNameNotNullFilterSelected && !isHomeScreenListRefreshing)
            homeScreenViewModel.clearNotNullFilter()
    }
    LaunchedEffect(isGroupByListIdFilterSelected) {
        if (isGroupByListIdFilterSelected && !isHomeScreenExpandableListRefreshing) {
            homeScreenViewModel.groupByListId()
        } else if (!isGroupByListIdFilterSelected && !isHomeScreenExpandableListRefreshing)
            homeScreenViewModel.clearGroupByFilter()
    }

    when (homeScreenData.value?.first?.restCallStatus?.first) {
        UiStatesModel.REST_CALL_Status.SUCCESS -> {

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Fetch") },
                        actions = {
                            HomeScreenTopAppBar(
                                isNotNullFilterSelected = isNameNotNullFilterSelected,
                                isGroupByListIdFilterSelected = isGroupByListIdFilterSelected,
                                filterNotNullCallback = {
                                    isHomeScreenListRefreshing = false
                                    isNameNotNullFilterSelected = true
                                    isGroupByListIdFilterSelected = false
                                },
                                groupByFilterCallback = {
                                    isHomeScreenExpandableListRefreshing = false
                                    isGroupByListIdFilterSelected = true
                                },
                                clearFilterNotNullCallback = {
                                    isHomeScreenListRefreshing = false
                                    isNameNotNullFilterSelected = false

                                },
                                clearGroupByFilterCallback = {
                                    isGroupByListIdFilterSelected = false
                                    isHomeScreenExpandableListRefreshing = false
                                }
                            )
                        }
                    )
                },
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
            ) {

                Box(modifier = Modifier.padding(it)) {
                    if (homeScreenGroupedData.value?.isNotEmpty() ?: false) {
                        homeScreenGroupedData.value?.let {
                            HomeScreenExpandableList(
                                items = it,
                                isRefreshing = isHomeScreenExpandableListRefreshing,
                                onRefresh = {
                                    isHomeScreenExpandableListRefreshing = true
                                }
                            )
                        }
                    } else {
                        homeScreenData.value?.second?.let {
                            HomeScreenList(items = it,
                                isRefreshing = isHomeScreenListRefreshing,
                                onRefresh = {
                                    isHomeScreenListRefreshing = true
                                })
                        }
                    }
                }
            }
        }


        UiStatesModel.REST_CALL_Status.LOADING -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularWavyProgressIndicator()
        }
        UiStatesModel.REST_CALL_Status.ERROR -> Text("${homeScreenData.value?.first?.restCallStatus?.second}")
        UiStatesModel.REST_CALL_Status.UNINITAIATED -> CircularWavyProgressIndicator()
        else -> Text("Bad request")
    }
}