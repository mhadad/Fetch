package com.hw.fetch.data.network

import com.hw.fetch.data.models.FetchHiringAssessmentModel
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("hiring.json")
    suspend fun getFetchHiringAssessmentData(): Response<List<FetchHiringAssessmentModel>>
}