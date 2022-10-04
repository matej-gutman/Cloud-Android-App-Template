package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.*
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Company
import com.templateapp.cloudapi.business.domain.models.Role
import dagger.Provides
import org.json.JSONObject


@Entity(
    tableName = "company_properties",
)
data class CompanyEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val _id: String,

    @ColumnInfo(name = "title")
    val title: String,


    )


fun CompanyEntity.toCompany(): Company {
    return Company(
        _id = _id,
        title = title,
    )
}

fun Company.toEntity(): CompanyEntity {
    return CompanyEntity(
        _id = _id,
        title = title,
    )
}