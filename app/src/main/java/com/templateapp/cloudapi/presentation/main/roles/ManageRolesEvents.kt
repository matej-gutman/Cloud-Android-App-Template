package com.templateapp.cloudapi.presentation.main.roles

import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.presentation.main.account.update.UpdateAccountEvents
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents


sealed class ManageRolesEvents{

    object GetRoles: ManageRolesEvents()

    object SetDoneToFalse: ManageRolesEvents()

    data class Error(val stateMessage: StateMessage): ManageRolesEvents()

    data class CheckDeleteRole(
        val _id: String
    ): ManageRolesEvents()

    data class Delete(
        val _id: String
    ): ManageRolesEvents()

    object OnRemoveHeadFromQueue: ManageRolesEvents()

    object NextPage: ManageRolesEvents()
}
