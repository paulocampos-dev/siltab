package com.prototype.silver_tab.data.models


data class DealerSummary(
    val dealerCode: String,
    val dealerName: String,
    val groupName: String? = null,
    val region: String? = null,
    val regionalManagerName: String? = null,
    val contactNumber: String? = null,
    val email: String? = null,
    val operationStatusName: String? = null,
    val operationServiceScopeName: String? = null,
    val operationAfterSale: String? = null
)