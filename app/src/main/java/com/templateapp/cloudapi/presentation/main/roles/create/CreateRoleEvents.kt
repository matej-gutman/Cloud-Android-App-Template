package com.templateapp.cloudapi.presentation.main.roles.create

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class CreateRoleEvents {

    data class PublishRole(
        val activity: FragmentActivity?
    ): CreateRoleEvents()

    data class OnUpdateTitle(
        val title: String,
    ): CreateRoleEvents()



    object OnPublishSuccess: CreateRoleEvents()

    data class Error(val stateMessage: StateMessage): CreateRoleEvents()

    object OnRemoveHeadFromQueue: CreateRoleEvents()
}










