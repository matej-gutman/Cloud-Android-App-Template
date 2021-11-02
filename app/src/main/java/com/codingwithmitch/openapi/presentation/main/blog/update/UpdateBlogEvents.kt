package com.codingwithmitch.openapi.presentation.main.blog.update

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.codingwithmitch.openapi.business.domain.util.StateMessage

sealed class UpdateBlogEvents {

    data class Update(val activity: FragmentActivity?): UpdateBlogEvents()

    data class getBlog(val pk: Int): UpdateBlogEvents()

    data class OnUpdateTitle(val title: String): UpdateBlogEvents()

    data class OnUpdateBody(val body: String): UpdateBlogEvents()

    data class OnUpdateUri(val uri: Uri): UpdateBlogEvents()

    object OnUpdateComplete: UpdateBlogEvents()

    data class Error(val stateMessage: StateMessage): UpdateBlogEvents()

    object OnRemoveHeadFromQueue: UpdateBlogEvents()
}




