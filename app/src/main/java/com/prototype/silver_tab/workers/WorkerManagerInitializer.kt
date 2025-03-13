package com.prototype.silver_tab.workers

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import com.prototype.silver_tab.SilverTabApplication
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import timber.log.Timber

/**
 * Custom initializer for WorkManager that uses Hilt to inject the WorkerFactory.
 * This allows Hilt to provide dependencies to Workers.
 */
class WorkManagerInitializer : Initializer<WorkManager> {

    override fun create(context: Context): WorkManager {
        Timber.d("Initializing WorkManager with Hilt integration")

        val workManagerConfiguration = getWorkManagerConfiguration(context)
        WorkManager.initialize(context, workManagerConfiguration)
        return WorkManager.getInstance(context)
    }

    private fun getWorkManagerConfiguration(context: Context): Configuration {
        // Use Entry Point to get the WorkManager Configuration from Hilt
        val workManagerEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WorkManagerInitializerEntryPoint::class.java
        )

        return workManagerEntryPoint.workManagerConfiguration()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        // No dependencies on other libraries
        return emptyList()
    }

    /**
     * Define an EntryPoint for retrieving the WorkManagerConfiguration from Hilt.
     */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkManagerInitializerEntryPoint {
        fun workManagerConfiguration(): Configuration
    }
}