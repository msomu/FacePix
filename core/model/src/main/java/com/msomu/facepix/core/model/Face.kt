package com.msomu.facepix.core.model

data class Face(
    val personId : Long? = null,
    val boundingBox : BoundingBox,
    val confidence : Float
)
