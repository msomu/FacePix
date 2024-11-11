package com.msomu.facepix.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class FaceTagWithPerson(
    @Embedded val faceTag: FaceTagEntity,
    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person: PersonEntity
)