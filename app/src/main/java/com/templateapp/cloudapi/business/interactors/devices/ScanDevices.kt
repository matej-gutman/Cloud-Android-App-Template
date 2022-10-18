package com.templateapp.cloudapi.business.interactors.devices

import android.util.Log
import com.templateapp.cloudapi.business.domain.models.Device
import com.templateapp.cloudapi.business.domain.util.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class ScanDevices(

) {

    private val TAG: String = "AppDebug"
    private var ranIt = false
    fun execute(
    ): Flow<DataState<List<Device>>> = flow {
        emit(DataState.loading<List<Device>>())
        // Scan for devices on the network

        Log.d(TAG, ranIt.toString())
        delay(6000)
        ranIt = true;
        // TODO: Implement. This is only a placeholder for fake devices.
        emit(DataState.data(
            response = null,
            data = listOf(
                Device(ip = "192.168.1.100", serialNumber = "SN-1000"),
                Device(ip = "192.168.1.101", serialNumber = "SN-1001"),
                Device(ip = "192.168.1.102", serialNumber = "SN-1002")

            )
        ))

    }
}

