package com.templateapp.cloudapi.presentation.main.create_report

import android.net.Uri
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class CreateReportState(
    val isLoading: Boolean = false,
    val title: String = "",
    val uri: Uri? = null,
    val onPublishSuccess: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)

