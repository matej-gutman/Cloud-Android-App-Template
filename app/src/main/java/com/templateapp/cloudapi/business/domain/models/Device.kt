package com.templateapp.cloudapi.business.domain.models

import com.templateapp.cloudapi.business.datasource.cache.account.RoleEntity

data class Device(
    val ip: String,
    val serialNumber : String
)