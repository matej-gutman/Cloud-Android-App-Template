package com.templateapp.cloudapi.presentation.main.report.list

import com.templateapp.cloudapi.business.domain.models.Report
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage
import com.templateapp.cloudapi.presentation.main.task.list.TaskFilterOptions.*
import com.templateapp.cloudapi.presentation.main.task.list.TaskOrderOptions.*

data class ReportState(
    val isLoading: Boolean = false,
    val reportList: List<Report> = listOf(),
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
