package com.prototype.silver_tab.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.prototype.silver_tab.SilverTabApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * TokenRefreshWorker is a WorkManager worker that periodically refreshes the auth token
 * to ensure it doesn't expire during app usage.
 */
class TokenRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    companion object {
        private const val TAG = "TokenRefreshWorker"
        private const val WORK_NAME = "token_refresh_work"

        /**
         * Schedule periodic token refresh
         */
        fun schedule(context: Context) {
            Log.d(TAG, "Scheduling token refresh work")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
                // Refresh every 15 minutes
                15, TimeUnit.MINUTES,
                // With a flex period of 5 minutes
                5, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }

        /**
         * Cancel scheduled token refresh
         */
        fun cancel(context: Context) {
            Log.d(TAG, "Canceling token refresh work")
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        /**
         * Request immediate token refresh (one-time)
         */
        fun refreshNow(context: Context) {
            Log.d(TAG, "Requesting immediate token refresh")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<TokenRefreshWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting token refresh work")

            val authRepository = SilverTabApplication.authRepository
            val currentState = authRepository.authState.first()

            if (!currentState.isAuthenticated) {
                Log.d(TAG, "Not authenticated, skipping token refresh")
                return@withContext Result.success()
            }

            val refreshResult = authRepository.refreshToken()

            return@withContext if (refreshResult.isSuccess) {
                Log.d(TAG, "Token refresh successful")
                Result.success()
            } else {
                Log.e(TAG, "Token refresh failed: ${refreshResult.exceptionOrNull()?.message}")
                // Retry on failures
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during token refresh", e)
            // Retry on unexpected errors
            Result.retry()
        }
    }
}