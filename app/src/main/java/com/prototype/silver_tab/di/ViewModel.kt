package com.prototype.silver_tab.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * This module provides dependencies specific to ViewModels.
 *
 * Note: Most ViewModel dependencies are now managed directly via constructor
 * injection, so this module is mostly empty. It's retained for potential
 * future use cases where we might need to provide custom implementations
 * for specific ViewModels.
 */
@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    // No manual providers needed as all dependencies are handled via constructor injection
}