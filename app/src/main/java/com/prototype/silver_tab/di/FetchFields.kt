package com.prototype.silver_tab.di

import com.prototype.silver_tab.config.FieldConfigService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FieldConfigModule {

    @Provides
    @Singleton
    fun provideFieldConfigService(): FieldConfigService {
        return FieldConfigService()
    }
}
