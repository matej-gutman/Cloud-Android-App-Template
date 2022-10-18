package com.templateapp.cloudapi.presentation.main.devices

import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Device
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class ManageDevicesState(
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
    val device: String = "",
    val deviceList: List<Device> = listOf(),
)
