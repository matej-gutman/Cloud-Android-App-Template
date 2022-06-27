package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.responseObjects.Report
import com.templateapp.cloudapi.business.datasource.network.responseObjects.Task

class ReportCreateUpdateResponse(

    @SerializedName("response")
    val response: String,

    @SerializedName("error")
    val error: String,

    @SerializedName("report")
    val report: Report,

    @SerializedName("owner")
    val owner: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,
)



fun ReportCreateUpdateResponse.toReport(): com.templateapp.cloudapi.business.domain.models.Report {
    return com.templateapp.cloudapi.business.domain.models.Report(
        _id = report._id,
        title = report.title,
        image = report.image,
        createdAt = DateUtils.convertServerStringDateToLong(
            report.createdAt
        ),
        updatedAt = DateUtils.convertServerStringDateToLong(
            report.updatedAt
        ),
        username = report.owner
    )
}













