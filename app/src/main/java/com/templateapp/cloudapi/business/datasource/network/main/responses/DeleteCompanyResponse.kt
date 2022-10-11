package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.datasource.network.main.AccountDto
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Company
import com.templateapp.cloudapi.business.domain.models.Role

class DeleteCompanyResponse (

    @SerializedName("response")
    val response: String,

    @SerializedName("company")
    val company: Company,


) {
    override fun toString(): String {
        return "DeleteCompanyResponse(response='$response', company=$company)"
    }
}


