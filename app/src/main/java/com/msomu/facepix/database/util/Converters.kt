package com.msomu.facepix.database.util

import android.graphics.RectF
import androidx.room.TypeConverter
import com.msomu.facepix.model.Face

class Converters {
    @TypeConverter
    fun fromFaceList(faces: List<Face>): String {
        return faces.joinToString("|") { face ->
            val box = face.boundingBox
            "${box.left},${box.top},${box.right},${box.bottom}:${face.confidence}"
        }
    }

    @TypeConverter
    fun toFaceList(data: String): List<Face> {
        if (data.isEmpty()) return emptyList()
        return data.split("|").map { value ->
            val (box, confidence) = value.split(":")
            val (left, top, right, bottom) = box.split(",").map { it.toFloat() }
            Face(boundingBox = RectF(left, top, right, bottom),
                confidence = confidence.toFloat())
        }
    }
}