package com.templateapp.cloudapi.presentation.main.companies

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

import kotlinx.coroutines.*
import androidx.lifecycle.viewModelScope
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.interactors.account.*
import com.templateapp.cloudapi.business.interactors.auth.GetDevice
import com.templateapp.cloudapi.business.interactors.companies.CheckDeleteCompany
import com.templateapp.cloudapi.business.interactors.companies.DeleteCompany
import com.templateapp.cloudapi.presentation.main.account.detail.AccountState
import com.templateapp.cloudapi.presentation.main.create_task.CreateTaskEvents
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents
import com.templateapp.cloudapi.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ManageCompaniesViewModel
@Inject
constructor(
    public val sessionManager: SessionManager,
    private val getCompanies: GetAllCompanies,
    private val checkCompanies: CheckDeleteCompany,
    private val delete: DeleteCompany,
    savedStateHandle: SavedStateHandle,
): ViewModel(){

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ManageCompaniesState> = MutableLiveData(ManageCompaniesState())
    init {
        onTriggerEvent(ManageCompaniesEvents.GetCompanies)
    }

    fun onTriggerEvent(event: ManageCompaniesEvents){
        when(event){

            is ManageCompaniesEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }

            is ManageCompaniesEvents.SetDoneToFalse -> {
                setDoneToFalse()
            }
            is ManageCompaniesEvents.GetCompanies -> {
                getCompanies()
            }

            is ManageCompaniesEvents.CheckDeleteCompany -> {
            checkCompanies(event._id)
        }
            is ManageCompaniesEvents.Delete -> {
                delete(event._id)
            }
            is ManageCompaniesEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ManageCompaniesEvents.NextPage -> {
                nextPage()
            }

        }
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }

    private fun nextPage() {
        incrementPageNumber()
        state.value?.let { state ->
            getCompanies.execute(
                authToken = sessionManager.state.value?.authToken,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(companiesList = list)

                }
                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun setDoneToFalse() {
        state.value?.let { state ->

                this.state.value = state.copy(done = false)


        }
    }

    private fun getCompanies() {

        state.value?.let { state ->
            getCompanies.execute(
                authToken = sessionManager.state.value?.authToken,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(companiesList = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }


    private fun checkCompanies(id: String) {

        state.value?.let { state ->

            checkCompanies.execute(
                authToken = sessionManager.state.value?.authToken,
                id = id,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(response = list)
                    if(list.message == SuccessHandling.SUCCESS_200){
                        this.state.value = list.users?.let { state.copy(accountList = it, id = id) }
                    }else if(list.message == SuccessHandling.SUCCESS_201){
                        this.state.value = list.users?.let { state.copy(accountList = it, id=id) }
                    }
                    else{

                        appendToMessageQueue(
                            stateMessage = StateMessage(list)
                        )
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){

                          onUpdateQueryExhausted(true)
                    }else{
                          appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)

        }


    }


    private fun delete(id: String) {

        state.value?.let { state ->

            this.state.value = state.copy(done = false)
            delete.execute(
                authToken = sessionManager.state.value?.authToken,
                id = id,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->

                    if(list.message == SuccessHandling.SUCCESS_COMPANY_DELETED){

                        this.state.value = state.copy(done = true)

                    }
                    else{

                        appendToMessageQueue(
                            stateMessage = StateMessage(list)
                        )
                    }
                }


            }.launchIn(viewModelScope)

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

}




















