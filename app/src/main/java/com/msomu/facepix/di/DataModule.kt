package com.msomu.facepix.di

import com.msomu.facepix.CompositePhotosRepository
import com.msomu.facepix.PhotosRepository
import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindPhotosRepository(
        compositePhotosRepository: CompositePhotosRepository
    ): PhotosRepository

}