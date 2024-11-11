package com.msomu.facepix.di

import com.msomu.facepix.CompositePhotosRepository
import com.msomu.facepix.ImageRepository
import com.msomu.facepix.ImageRepositoryImpl
import com.msomu.facepix.PhotosRepository
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