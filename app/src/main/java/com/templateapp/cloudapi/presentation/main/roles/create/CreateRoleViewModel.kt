package com.templateapp.cloudapi.presentation.main.roles.create

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_ROLE_CREATED
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_TASK_CREATED
import com.templateapp.cloudapi.business.interactors.account.PublishRole
import com.templateapp.cloudapi.business.interactors.task.PublishTask
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreateRoleViewModel
@Inject
constructor(
    private val publishRole: PublishRole,
    private val sessionManager: SessionManager
    //private val baseApplication: BaseApplication
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CreateRoleState> = MutableLiveData(CreateRoleState())

    //@Inject
    //lateinit var baseApplication: BaseApplication

    fun onTriggerEvent(event: CreateRoleEvents){
        when(event){
            is CreateRoleEvents.OnUpdateTitle -> {
                onUpdateTitle(event.title)
            }

            is CreateRoleEvents.PublishRole -> {
                publishRole(event.activity)
            }
            is CreateRoleEvents.OnPublishSuccess -> {
                onPublishSuccess()
            }
            is CreateRoleEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CreateRoleEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
        }
    }

    private fun removeHeadFromQueue(){
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

    // call after successfully publishing
    private fun clearNewRoleFields(){
        onUpdateTitle("")
    }

    private fun onPublishSuccess(){
        clearNewRoleFields()
        state.value?.let { state ->
            this.state.value = state.copy(onPublishSuccess = true)
        }
    }

    private fun onUpdateTitle(title: String){
        state.value?.let { state ->
            this.state.value = state.copy(title = title)
        }
    }


    private fun publishRole(activity: FragmentActivity?){
        state.value?.let { state ->
            val title = state.title
            val completed = state.completed


                    publishRole.execute(
                        authToken = sessionManager.state.value?.authToken,
                        title = title,

                    ).onEach { dataState ->
                        this.state.value = state.copy(isLoading = dataState.isLoading)

                        dataState.data?.let { response ->
                            if(response.message == SUCCESS_ROLE_CREATED){
                                onTriggerEvent(CreateRoleEvents.OnPublishSuccess)
                            }else{
                                appendToMessageQueue(
                                    stateMessage = StateMessage(response)
                                )
                            }
                        }

                        dataState.stateMessage?.let { stateMessage ->
                            appendToMessageQueue(stateMessage)
                        }
                    }.launchIn(viewModelScope)
                }


    }
}





