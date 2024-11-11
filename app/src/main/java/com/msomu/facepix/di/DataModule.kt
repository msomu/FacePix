package com.msomu.facepix.di

import com.msomu.facepix.core.data.CompositePhotosRepository
import com.msomu.facepix.core.data.ImageRepository
import com.msomu.facepix.core.data.ImageRepositoryImpl
import com.msomu.facepix.core.data.PhotosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindPhotosRepository(
        compositePhotosRepository: CompositePhotosRepository
    ): PhotosRepository

    @Binds
    abstract fun bindImageRepository(
        imageRepository: ImageRepositoryImpl
    ): ImageRepository

}