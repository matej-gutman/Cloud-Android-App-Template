package com.templateapp.cloudapi.business.datasource.network.responseObjects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.templateapp.cloudapi.business.domain.models.Company
import com.templateapp.cloudapi.business.domain.models.Role

class User(

    @SerializedName("age")
    @Expose
    var age: Int,

    @SerializedName("_id")
    @Expose
    var _id: String,

    @SerializedName("username")
    @Expose
    var username: String,

    @SerializedName("firstName")
    @Expose
    var firstName: String,

    @SerializedName("lastName")
    @Expose
    var lastName: String,

    @SerializedName("email")
    @Expose
    var email: String,


    @SerializedName("enabled")
    @Expose
    var enabled: Boolean,

    @SerializedName("createdAt")
    @Expose
    var createdAt: String,

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String,

    @SerializedName("role")
    @Expose
    var role: Role,



    @SerializedName("company")
    @Expose
    var company: Company,

    @SerializedName("userCreatedSequence")
    @Expose
    var userCreatedSequence: Int,

    @SerializedName("__v")
    @Expose
    var __v: Int

)