package com.hw.fetch.domain.view_models

import android.util.Log
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hw.fetch.data.models.FetchHiringAssessmentModel
import com.hw.fetch.data.models.UiStatesModel
import com.hw.fetch.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel()
class HomeScreenViewModel @Inject constructor(val restAPIService: ApiService): ViewModel() {
    private val _homeScreenData : MutableLiveData<Pair<UiStatesModel, List<FetchHiringAssessmentModel>?>> =
        MutableLiveData(Pair(UiStatesModel(Pair(UiStatesModel.REST_CALL_Status.UNINITAIATED, null)), null))
    val homeScreenData : LiveData<Pair<UiStatesModel, List<FetchHiringAssessmentModel>?>> = _homeScreenData
    val restAPI_Semaphore = Semaphore(1)
    var fetchHiringssessmentList : List<FetchHiringAssessmentModel>? = emptyList()
    var fetchHiringAssessmentMap : Map<Int, List<FetchHiringAssessmentModel>> = emptyMap()
    private val _homeScreenDataGrouped : MutableLiveData<Map<Int, List<FetchHiringAssessmentModel>>> =
        MutableLiveData(emptyMap<Int, List<FetchHiringAssessmentModel>>())
    val homeScreenDataGrouped = _homeScreenDataGrouped
    suspend fun loadHomeScreenData() {
        withTimeout(1000) {
            if (restAPI_Semaphore.tryAcquire()) {
                runCatching {
                    viewModelScope.launch(Dispatchers.IO) {
                        ensureActive()
                        fetchHiringssessmentList = emptyList()
                        _homeScreenData.postValue(
                            Pair(
                                UiStatesModel(
                                    Pair(
                                        UiStatesModel.REST_CALL_Status.LOADING,
                                        null
                                    )
                                ), null
                            )
                        )
                        val homeScrenRes = restAPIService.getFetchHiringAssessmentData()
                        when {
                            homeScrenRes.isSuccessful -> {
                                fetchHiringssessmentList = homeScrenRes.body()?.let {
                                    it.sortedBy { it.listId }.sortedBy { it.name }
                                }
                                _homeScreenData.postValue(
                                    Pair(
                                        UiStatesModel(
                                            Pair(
                                                UiStatesModel.REST_CALL_Status.SUCCESS, null
                                            )
                                        ),
                                            fetchHiringssessmentList
                                        )
                                )
                            }
                            homeScrenRes.errorBody() != null -> _homeScreenData.postValue(
                                Pair(
                                    UiStatesModel(
                                        Pair(
                                            UiStatesModel.REST_CALL_Status.ERROR,
                                            homeScrenRes.message()
                                        )
                                    ), null
                                )
                            )
                        }
                    }
                }.onSuccess {
                    restAPI_Semaphore.release()
                }.onFailure { throwable ->
                    _homeScreenData.postValue(
                        Pair(
                            UiStatesModel(
                                Pair(
                                    UiStatesModel.REST_CALL_Status.ERROR, throwable.message
                                )
                            ), null
                        )
                    )
                }
            }
            else {
                _homeScreenData.postValue(
                    Pair(
                        UiStatesModel(
                            Pair(
                                UiStatesModel.REST_CALL_Status.ERROR, "Could not acquire sempahore to send a network request"
                            )
                        ), null
                    )
                )
            }
        }
    }
    fun filterNameNotNull_Blank(){
        viewModelScope.launch(Dispatchers.Unconfined) {
            ensureActive()
            _homeScreenData.postValue(Pair(UiStatesModel(Pair(UiStatesModel.REST_CALL_Status.LOADING, null)), null))
            fetchHiringssessmentList = fetchHiringssessmentList?.let {
                it.filter { !it.name.isNullOrBlank() && !it.name.trim().isEmpty() && !it.name.trim().equals("null") }
            }
            _homeScreenData.postValue(Pair(UiStatesModel(Pair(UiStatesModel.REST_CALL_Status.SUCCESS, null)), fetchHiringssessmentList))
        }
    }

    fun groupByListId(){
        viewModelScope.launch(Dispatchers.Unconfined){
            if(fetchHiringssessmentList?.isEmpty() ?: true)
                loadHomeScreenData()
            fetchHiringAssessmentMap = fetchHiringssessmentList?.let {
                it.groupBy { it.listId!! } }!!
            _homeScreenDataGrouped.postValue(fetchHiringAssessmentMap)
            }

    }

    fun clearGroupByFilter(){
        viewModelScope.launch(Dispatchers.Unconfined){
            ensureActive()
            fetchHiringAssessmentMap = emptyMap()
            _homeScreenDataGrouped.postValue(fetchHiringAssessmentMap)
        }
    }
    fun clearNotNullFilter(){
        viewModelScope.launch(Dispatchers.IO){
            ensureActive()
            loadHomeScreenData()
        }
    }
}