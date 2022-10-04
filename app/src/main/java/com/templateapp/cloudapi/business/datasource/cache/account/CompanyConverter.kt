package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.TypeConverter
import com.templateapp.cloudapi.business.domain.models.Company
import com.templateapp.cloudapi.business.domain.models.Role
import org.json.JSONObject

class CompanyConverter {

    @TypeConverter
    fun fromSource(source: Company?): String {
        return JSONObject().apply {
            if (source != null) {
                put("_id", source._id)
            }
            if (source != null) {
                put("title", source.title)
            }
        }.toString()
    }

    @TypeConverter
    fun toSource(source: String?): Company? {

        if (source != "{}") {
            val json = JSONObject(source)
        return Company(json.getString("_id"), json.getString("title"))
        }else{
            return Company("", "")
        }
    }
}