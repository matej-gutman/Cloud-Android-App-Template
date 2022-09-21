package com.templateapp.cloudapi.presentation.main.devices

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

import kotlinx.coroutines.*
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.interactors.account.GetAccount
import com.templateapp.cloudapi.business.interactors.account.GetAccountFromCache
import com.templateapp.cloudapi.business.interactors.account.GetAllUsers
import com.templateapp.cloudapi.business.interactors.account.UpdateAccount
import com.templateapp.cloudapi.business.interactors.devices.ScanDevices
import com.templateapp.cloudapi.presentation.main.account.detail.AccountState
import com.templateapp.cloudapi.presentation.main.task.detail.ViewTaskEvents
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ManageDeviceViewModel
@Inject
constructor(
    private val getDevices: ScanDevices,
    savedStateHandle: SavedStateHandle,
): ViewModel(){

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ManageDevicesState> = MutableLiveData(ManageDevicesState())

    init {
        // TODO: Implement saved state!
        //savedStateHandle.get<String>("taskId")?.let { taskId ->
            onTriggerEvent(ManageDevicesEvents.GetDevice)
        //}
    }


    fun onTriggerEvent(event: ManageDevicesEvents){
        when(event){

            is ManageDevicesEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }

            is ManageDevicesEvents.GetDevice -> {
                getDevices()
            }
            is ManageDevicesEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }

        }
    }

    private fun getDevices() {
        Log.d(TAG, "Getting devices ...")
    }

    private fun removeHeadFromQueue(){
        Log.d(TAG, "Getting devices - removeHeadFromQueue")
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            }catch (e: Exception){
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        Log.d(TAG, "Getting devices - appendToMessageQueue")

        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(!(stateMessage.response.uiComponentType is UIComponentType.None)){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

}
