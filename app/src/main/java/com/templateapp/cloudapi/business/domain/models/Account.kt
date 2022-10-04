package com.templateapp.cloudapi.business.domain.models

import com.templateapp.cloudapi.business.datasource.cache.account.RoleEntity

data class Account(
    val _id: String,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val createdAt: String,
    val updatedAt: String,
    val userCreatedSequence: Int,
    val enabled: Boolean,
    val __v: Int,
    val role: Role,
    val company: Company?,
)









