package com.msomu.facepix.model

import android.graphics.RectF

data class ImageResource(
    val imagePath : String,
    val height : Int,
    val width : Int,
    val faces : List<Face> = emptyList()
)

data class Face(
    val personId : Long? = null,
    val boundingBox : RectF,
    val confidence : Float
)