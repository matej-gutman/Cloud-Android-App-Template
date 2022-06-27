package com.templateapp.cloudapi.presentation.main.create_report

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.templateapp.cloudapi.business.domain.util.StateMessage

sealed class CreateReportEvents {

    data class PublishReport(
        val activity: FragmentActivity?
    ): CreateReportEvents()

    data class OnUpdateTitle(
        val title: String,
    ): CreateReportEvents()

    data class OnUpdateUri(
        val uri: Uri,
    ): CreateReportEvents()


    object OnPublishSuccess: CreateReportEvents()

    data class Error(val stateMessage: StateMessage): CreateReportEvents()

    object OnRemoveHeadFromQueue: CreateReportEvents()
}










