package com.templateapp.cloudapi.business.datasource.cache.report

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.templateapp.cloudapi.business.domain.models.Report
import com.templateapp.cloudapi.business.domain.models.Task

@Entity(tableName = "report")
data class ReportEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val _id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "image")
    val image: String,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long,

    @ColumnInfo(name = "username")
    val username: String
)

fun ReportEntity.toReport(): Report{
    return Report(
        _id = _id,
        title = title,
        image = image,
        createdAt = createdAt,
        updatedAt = updatedAt,
        username = username
    )
}

fun Report.toEntity(): ReportEntity{
    return ReportEntity(
        _id = _id,
        title = title,
        image = image,
        createdAt = createdAt,
        updatedAt = updatedAt,
        username = username
    )
}











