package com.msomu.facepix.core.database.model

import com.msomu.facepix.core.model.Face

data class TaggedFaceInfo(
    val face: Face,
    val personEntity: PersonEntity? = null
)