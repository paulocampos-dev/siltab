package com.prototype.silver_tab.data.api_connection.routes

import com.prototype.silver_tab.data.models.DealerSummary
import retrofit2.http.GET

interface DealerApi {
    @GET("dealer/user/dealer-summary")
    suspend fun getDealerSummary(): List<DealerSummary>
}