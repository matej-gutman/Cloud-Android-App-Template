package com.templateapp.cloudapi.business.domain.models

data class Company (
    val _id: String,
    val title: String,
) {
    override fun toString(): String {
        return title
    }
}


