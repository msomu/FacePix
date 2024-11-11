package com.msomu.facepix.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.msomu.facepix.database.model.ProcessedImageEntity
import com.msomu.facepix.database.model.ProcessedImageWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcessedImageDao {
    @Query("SELECT * FROM processed_images")
    fun getAllImages(): Flow<List<ProcessedImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ProcessedImageEntity)

    @Query("DELETE FROM processed_images WHERE imagePath = :path")
    suspend fun deleteImage(path: String)

    @Query("SELECT * FROM processed_images WHERE imagePath = :path")
    suspend fun getImage(path: String): ProcessedImageEntity?

    @Transaction
    @Query("SELECT * FROM processed_images WHERE imagePath = :path")
    fun getImageWithTags(path: String): Flow<ProcessedImageWithTags?>

    @Transaction
    @Query("SELECT * FROM processed_images")
    fun getAllImagesWithTags(): Flow<List<ProcessedImageWithTags>>
}