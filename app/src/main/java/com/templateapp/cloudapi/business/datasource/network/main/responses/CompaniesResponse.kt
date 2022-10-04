package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.domain.models.Company
import com.templateapp.cloudapi.business.domain.models.Role

class CompaniesResponse (
    @SerializedName("companies")
    var companies: List<CompanyDto>,

    @SerializedName("count")
    var count: Int
)


fun CompaniesResponse.toList(): List<Company>{
    val list: MutableList<Company> = mutableListOf()
    for(dto in companies){
        list.add(
            dto.toCompany()
        )
    }
    return list
}

