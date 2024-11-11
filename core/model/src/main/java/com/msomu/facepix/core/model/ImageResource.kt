package com.msomu.facepix.core.model

data class ImageResource(
    val imagePath : String,
    val height : Int,
    val width : Int,
    val faces : List<Face> = emptyList()
)