package com.templateapp.cloudapi.business.domain.models

data class Report(
    val _id: String,
    val title: String,
    val image: String,
    val createdAt: Long,
    val updatedAt: Long,
    val username: String
)