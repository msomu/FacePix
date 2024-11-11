package com.msomu.facepix.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProcessedImageWithTags(
    @Embedded val image: ProcessedImageEntity,
    @Relation(
        entity = FaceTagEntity::class,
        parentColumn = "imagePath",
        entityColumn = "imagePath"
    )
    val faceTags: List<FaceTagWithPerson>
)
