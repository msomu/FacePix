package com.msomu.facepix.core.database.di

import com.msomu.facepix.core.database.AppDatabase
import com.msomu.facepix.core.database.dao.PersonDao
import com.msomu.facepix.core.database.dao.ProcessedImageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesTopicsDao(
        database: AppDatabase,
    ): ProcessedImageDao = database.processedImageDao()

    @Provides
    fun providesPersonDao(
        database: AppDatabase,
    ): PersonDao = database.personDao()
}
