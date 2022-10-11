package com.templateapp.cloudapi.presentation.main.companies.create

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class CreateCompanyEvents {

    data class PublishCompany(
        val activity: FragmentActivity?
    ): CreateCompanyEvents()

    data class OnUpdateTitle(
        val title: String,
    ): CreateCompanyEvents()



    object OnPublishSuccess: CreateCompanyEvents()

    data class Error(val stateMessage: StateMessage): CreateCompanyEvents()

    object OnRemoveHeadFromQueue: CreateCompanyEvents()
}










