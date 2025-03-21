package com.prototype.silver_tab.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.prototype.silver_tab.data.repository.AuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.BackoffPolicy

/**
 * TokenRefreshWorker is a WorkManager worker that periodically refreshes the auth token
 * to ensure it doesn't expire during app usage.
 *
 * Updated to use Hilt for dependency injection.
 */
@HiltWorker
class TokenRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val authRepository: AuthRepository
) : CoroutineWorker(context, params) {
    companion object {
        private const val TAG = "TokenRefreshWorker"
        private const val WORK_NAME = "token_refresh_work"

        /**
         * Schedule periodic token refresh
         */
        fun schedule(context: Context) {
            Timber.d("Scheduling token refresh work")

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
                // Refresh every 14 minutes
                14, TimeUnit.MINUTES,
                // With a flex period of 4 minutes
                4, TimeUnit.MINUTES
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

            Timber.d("Token refresh work scheduled successfully")
        }

        /**
         * Cancel scheduled token refresh
         */
        fun cancel(context: Context) {
            Timber.d("Canceling token refresh work")
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        /**
         * Request immediate token refresh (one-time)
         */
        fun refreshNow(context: Context) {
            Timber.d("Requesting immediate token refresh")

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
            Timber.d("Starting token refresh work")

            val currentState = authRepository.authState.first()

            if (!currentState.isAuthenticated) {
                Timber.d("Not authenticated, skipping token refresh")
                return@withContext Result.success()
            }

            val refreshResult = authRepository.refreshToken()

            return@withContext if (refreshResult.isSuccess) {
                Timber.d("Token refresh successful")
                Result.success()
            } else {
                Timber.e("Token refresh failed: ${refreshResult.exceptionOrNull()?.message}")
                // Retry on failures
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during token refresh")
            // Retry on unexpected errors
            Result.retry()
        }
    }
}