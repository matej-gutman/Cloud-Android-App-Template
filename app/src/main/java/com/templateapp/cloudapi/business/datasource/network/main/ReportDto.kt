package com.templateapp.cloudapi.business.datasource.network.main

import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.User
import com.templateapp.cloudapi.business.domain.models.Report

class ReportDto(

    @SerializedName("_id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("owner")
    val owner: User,

    @SerializedName("error")
    val error: String?,
)

fun ReportDto.toReport(): Report{
    return Report(
            _id = id,
            title = title,
            image = image,
            createdAt = DateUtils.convertServerStringDateToLong(createdAt),
            updatedAt = DateUtils.convertServerStringDateToLong(updatedAt),
            username = owner.name
        )
}

