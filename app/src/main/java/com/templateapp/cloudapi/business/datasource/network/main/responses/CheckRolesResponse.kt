package com.templateapp.cloudapi.business.datasource.network.main.responses

import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.datasource.network.main.AccountDto
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Role

class CheckRolesResponse (
    @SerializedName("users")
    var users: List<AccountDto>,

    @SerializedName("response")
    val response: String,

)


fun CheckRolesResponse.toList(): List<Account>{
    val list: MutableList<Account> = mutableListOf()
    for(dto in users){
        list.add(
            dto.toAccount()
        )
    }
    return list
}

