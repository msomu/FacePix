package com.msomu.facepix.model

import com.msomu.facepix.core.model.Face

data class ImageResource(
    val imagePath : String,
    val height : Int,
    val width : Int,
    val faces : List<Face> = emptyList()
)