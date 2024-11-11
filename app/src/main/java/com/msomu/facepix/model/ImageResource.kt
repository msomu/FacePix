package com.msomu.facepix.model

import android.graphics.RectF
import androidx.room.ForeignKey
import com.msomu.facepix.database.model.PersonEntity

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