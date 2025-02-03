package com.prototype.silver_tab.data.api

import com.prototype.silver_tab.data.models.PDI
import retrofit2.http.GET

interface PdiApi {

    @GET("/pdi/inspector/1")
    suspend fun getPdi(

    ): List<PDI>
}