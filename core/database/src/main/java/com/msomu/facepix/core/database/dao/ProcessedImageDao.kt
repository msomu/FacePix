package com.msomu.facepix.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.msomu.facepix.core.database.model.ProcessedImageEntity
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
    suspend fun getImageFromPath(path: String): ProcessedImageEntity?

    @Query("SELECT * FROM processed_images WHERE imagePath = :path")
    fun getImage(path: String): Flow<ProcessedImageEntity?>

    @Update
    suspend fun updateProcessedImage(image: ProcessedImageEntity)
}