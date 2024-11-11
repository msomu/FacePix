package com.msomu.facepix.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.msomu.facepix.database.model.FaceTagEntity
import com.msomu.facepix.database.model.FaceTagWithPerson
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceTagDao {
    @Query("SELECT * FROM face_tags WHERE imagePath = :imagePath")
    fun getFaceTagsForImage(imagePath: String): Flow<List<FaceTagWithPerson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaceTag(faceTag: FaceTagEntity)

    @Delete
    suspend fun deleteFaceTag(faceTag: FaceTagEntity)
}
