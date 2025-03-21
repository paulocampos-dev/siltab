package com.prototype.silver_tab.data.routes

import com.prototype.silver_tab.data.models.DealerSummary
import retrofit2.Response
import retrofit2.http.GET

interface DealerApi {
    @GET("dealer/user/dealer-summary")
    suspend fun getDealerSummary(): Response<List<DealerSummary>>
}