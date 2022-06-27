package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.templateapp.cloudapi.business.datasource.network.main.TaskDto
import com.templateapp.cloudapi.business.datasource.network.main.toTask
import com.templateapp.cloudapi.business.domain.models.Task
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.network.main.ReportDto
import com.templateapp.cloudapi.business.datasource.network.main.toReport
import com.templateapp.cloudapi.business.domain.models.Report

class ReportListSearchResponse(

    @SerializedName("results")
    var results: List<ReportDto>,

    @SerializedName("count")
    var detail: String
)

fun ReportListSearchResponse.toList(): List<Report>{
    val list: MutableList<Report> = mutableListOf()
    for(dto in results){
        list.add(
            dto.toReport()
        )
    }
    return list
}






