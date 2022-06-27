package com.templateapp.cloudapi.presentation.main.create_report

import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.SuccessHandling.Companion.SUCCESS_TASK_CREATED
import com.templateapp.cloudapi.business.interactors.report.PublishReport
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
class CreateReportViewModel
@Inject
constructor(
    private val publishReport: PublishReport,
    private val sessionManager: SessionManager
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CreateReportState> = MutableLiveData(CreateReportState())


    fun onTriggerEvent(event: CreateReportEvents){
        when(event){
            is CreateReportEvents.OnUpdateTitle -> {
                onUpdateTitle(event.title)
            }
            is CreateReportEvents.OnUpdateUri -> {
                onUpdateUri(event.uri)
            }
            is CreateReportEvents.PublishReport -> {
                publishReport(event.activity)
            }
            is CreateReportEvents.OnPublishSuccess -> {
                onPublishSuccess()
            }
            is CreateReportEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CreateReportEvents.OnRemoveHeadFromQueue ->{
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
    private fun clearNewTaskFields(){
        onUpdateTitle("")
        onUpdateUri(null)
    }

    private fun onPublishSuccess(){
        clearNewTaskFields()
        state.value?.let { state ->
            this.state.value = state.copy(onPublishSuccess = true)
        }
    }

    private fun onUpdateUri(uri: Uri?){
        state.value?.let { state ->
            this.state.value = state.copy(uri = uri)
        }
    }

    private fun onUpdateTitle(title: String){
        state.value?.let { state ->
            this.state.value = state.copy(title = title)
        }
    }
    private fun publishReport(activity: FragmentActivity?){
        state.value?.let { state ->
            val title = RequestBody.create(
                MediaType.parse("text/plain"),
                state.title
            )

            if(state.uri == null){
                onTriggerEvent(CreateReportEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = ErrorHandling.ERROR_MUST_SELECT_IMAGE,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                ))
            }
            else{
                var multipartBody: MultipartBody.Part? = null
                state.uri?.let { contentFilePath ->
                    val filename = contentFilePath.path?.split("/")?.lastOrNull()
                    val imageFile = activity?.contentResolver?.openInputStream(contentFilePath)

                    imageFile?.let{
                        val requestBody =
                            RequestBody.create(
                                MediaType.parse("image/*"),
                                imageFile.readBytes()
                            )
                        multipartBody = MultipartBody.Part.createFormData(
                            "image",
                            filename,
                            requestBody
                        )
                    }
                }

                if(multipartBody != null){
                    publishReport.execute(
                        authToken = sessionManager.state.value?.authToken,
                        title = title,
                        image = multipartBody,
                    ).onEach { dataState ->
                        this.state.value = state.copy(isLoading = dataState.isLoading)

                        dataState.data?.let { response ->
                            if(response.message == SUCCESS_TASK_CREATED){
                                onTriggerEvent(CreateReportEvents.OnPublishSuccess)
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
    }
}





