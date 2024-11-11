package com.msomu.facepix.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.msomu.facepix.model.Face

@Entity(tableName = "processed_images")
data class ProcessedImageEntity(
    @PrimaryKey val imagePath: String,
    val height: Int,
    val width: Int,
    val lastModified: Long, // To track if image has been updated
    val detectedFaces: List<Face>
)