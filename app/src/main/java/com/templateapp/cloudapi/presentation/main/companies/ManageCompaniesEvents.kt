package com.templateapp.cloudapi.presentation.main.companies

import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.presentation.main.account.update.UpdateAccountEvents
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents


sealed class ManageCompaniesEvents{

    object GetCompanies: ManageCompaniesEvents()

    object SetDoneToFalse: ManageCompaniesEvents()

    data class Error(val stateMessage: StateMessage): ManageCompaniesEvents()

    data class CheckDeleteCompany(
        val _id: String
    ): ManageCompaniesEvents()

    data class Delete(
        val _id: String
    ): ManageCompaniesEvents()

    object OnRemoveHeadFromQueue: ManageCompaniesEvents()

    object NextPage: ManageCompaniesEvents()
}
