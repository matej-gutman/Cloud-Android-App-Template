package com.templateapp.cloudapi.business.datasource.network.responseObjects

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Report(
    @SerializedName("_id")
    @Expose
    var _id: String,

    @SerializedName("title")
    @Expose
    var title: String,

    @SerializedName("owner")
    @Expose
    var owner: String,

    @SerializedName("image")
    @Expose
    var image: String,

    @SerializedName("imageBuffer")
    @Expose
    var imageBuffer: DataBuffer,

    @SerializedName("createdAt")
    @Expose
    var createdAt: String,

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String,

    @SerializedName("taskCreatedSequence")
    @Expose
    var taskCreatedSequence: Long,

    @SerializedName("__v")
    @Expose
    var __v: Long

)