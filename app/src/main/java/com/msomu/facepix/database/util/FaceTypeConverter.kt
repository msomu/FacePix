package com.msomu.facepix.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.msomu.facepix.model.Face

class FaceTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromFaceList(faces: List<Face>): String {
        return gson.toJson(faces)
    }

    @TypeConverter
    fun toFaceList(facesString: String): List<Face> {
        val listType = object : TypeToken<List<Face>>() {}.type
        return gson.fromJson(facesString, listType)
    }
}