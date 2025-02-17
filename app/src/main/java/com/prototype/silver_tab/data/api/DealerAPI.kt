package com.prototype.silver_tab.data.api

import com.prototype.silver_tab.ui.components.DealerSummary
import retrofit2.http.GET

interface DealerApi {
    @GET("/dealer/user/dealer-summary")
    suspend fun getDealerSummary(): List<DealerSummary>
}