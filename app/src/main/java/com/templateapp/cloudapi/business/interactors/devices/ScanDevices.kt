package com.templateapp.cloudapi.business.interactors.devices

import com.templateapp.cloudapi.business.domain.models.Device
import com.templateapp.cloudapi.business.domain.util.DataState
import kotlinx.coroutines.flow.*

class ScanDevices(

) {

    private val TAG: String = "AppDebug"

    fun execute(
    ): Flow<DataState<List<Device>>> = flow {
        emit(DataState.loading<List<Device>>())
        // Scan for devices on the network
        // TODO: Implememt. See SearchTasks as an example of flow.
        //emit(DataState.data(response = null, data = cachedTasks))
    }
}

