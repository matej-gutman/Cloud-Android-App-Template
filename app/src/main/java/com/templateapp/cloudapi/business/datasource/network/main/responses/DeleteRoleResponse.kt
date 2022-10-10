package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.datasource.network.main.AccountDto
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Role

class DeleteRoleResponse (

    @SerializedName("response")
    val response: String,

)


