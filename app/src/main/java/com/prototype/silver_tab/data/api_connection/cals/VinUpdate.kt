package com.prototype.silver_tab.data.api_connection.cals

import android.util.Log
import android.widget.Toast
import com.prototype.silver_tab.SilverTabApplication
import com.prototype.silver_tab.data.models.InspectionInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

fun submitVinCorrection(inspectionInfo: InspectionInfo, newVin: String) {
    // This is a placeholder for your API call
    // You'll need to implement this based on your API structure
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    coroutineScope.launch {
        try {
            withContext(Dispatchers.IO) {
                // Example API call - replace with your actual implementation
                // RetrofitClient.carsApi.updateCarVin(inspectionInfo.chassi, newVin)

                // For now, just log the intended action
                Timber.tag("VinCorrection")
                    .d("Updating VIN from ${inspectionInfo.chassi} to $newVin")
            }

            // Show success message
            Toast.makeText(
                SilverTabApplication.instance,
                "VIN correction submitted successfully",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            // Handle error
            Timber.tag("VinCorrection").e("Error updating VIN: ${e.message}")

            // Show error message
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    SilverTabApplication.instance,
                    "Error updating VIN: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}