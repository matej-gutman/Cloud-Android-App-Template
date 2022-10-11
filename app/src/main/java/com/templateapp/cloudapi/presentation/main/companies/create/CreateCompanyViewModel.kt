package com.templateapp.cloudapi.presentation.main.companies.create

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
import com.templateapp.cloudapi.business.interactors.companies.PublishCompany
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
class CreateCompanyViewModel
@Inject
constructor(
    private val publishCompany: PublishCompany,
    private val sessionManager: SessionManager
    //private val baseApplication: BaseApplication
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CreateCompanyState> = MutableLiveData(CreateCompanyState())

    //@Inject
    //lateinit var baseApplication: BaseApplication

    fun onTriggerEvent(event: CreateCompanyEvents){
        when(event){
            is CreateCompanyEvents.OnUpdateTitle -> {
                onUpdateTitle(event.title)
            }

            is CreateCompanyEvents.PublishCompany -> {
                publishCompany(event.activity)
            }
            is CreateCompanyEvents.OnPublishSuccess -> {
                onPublishSuccess()
            }
            is CreateCompanyEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CreateCompanyEvents.OnRemoveHeadFromQueue ->{
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
    private fun clearNewCompanyFields(){
        onUpdateTitle("")
    }

    private fun onPublishSuccess(){
        clearNewCompanyFields()
        state.value?.let { state ->
            this.state.value = state.copy(onPublishSuccess = true)
        }
    }

    private fun onUpdateTitle(title: String){
        state.value?.let { state ->
            this.state.value = state.copy(title = title)
        }
    }


    private fun publishCompany(activity: FragmentActivity?){
        state.value?.let { state ->
            val title = state.title
            val completed = state.completed


                    publishCompany.execute(
                        authToken = sessionManager.state.value?.authToken,
                        title = title,

                    ).onEach { dataState ->
                        this.state.value = state.copy(isLoading = dataState.isLoading)

                        dataState.data?.let { response ->
                            if(response.message == SUCCESS_ROLE_CREATED){
                                onTriggerEvent(CreateCompanyEvents.OnPublishSuccess)
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





