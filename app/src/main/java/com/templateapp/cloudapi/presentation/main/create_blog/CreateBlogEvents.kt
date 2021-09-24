package com.templateapp.cloudapi.presentation.main.create_blog

import android.net.Uri
import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class CreateBlogEvents {

    object PublishBlog: CreateBlogEvents()

    data class OnUpdateTitle(
        val title: String,
    ): CreateBlogEvents()

    data class OnUpdateBody(
        val body: String,
    ): CreateBlogEvents()

    data class OnUpdateUri(
        val uri: Uri,
    ): CreateBlogEvents()


    object OnPublishSuccess: CreateBlogEvents()

    data class Error(val stateMessage: StateMessage): CreateBlogEvents()

    object OnRemoveHeadFromQueue: CreateBlogEvents()
}










