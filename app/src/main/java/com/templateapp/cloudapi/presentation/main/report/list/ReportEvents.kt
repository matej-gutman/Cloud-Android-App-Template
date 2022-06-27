package com.templateapp.cloudapi.presentation.main.report.list

import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents


sealed class ReportEvents {

    object NewSearch : ReportEvents()

    object NextPage: ReportEvents()

    data class Error(val stateMessage: StateMessage): ReportEvents()

    object OnRemoveHeadFromQueue: ReportEvents()
}
