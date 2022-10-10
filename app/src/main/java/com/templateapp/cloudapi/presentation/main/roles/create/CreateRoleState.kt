package com.templateapp.cloudapi.presentation.main.roles.create

import android.net.Uri
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class CreateRoleState(
    val isLoading: Boolean = false,
    val title: String = "",
    val completed: Boolean = false,
    val onPublishSuccess: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)

