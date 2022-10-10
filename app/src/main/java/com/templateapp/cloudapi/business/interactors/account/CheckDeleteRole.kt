package com.templateapp.cloudapi.business.interactors.account

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.cache.account.*
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.auth.toEntity
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.returnOrderedTaskQuery
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.datasource.cache.task.toTask
import com.templateapp.cloudapi.business.datasource.network.main.responses.toList
import com.templateapp.cloudapi.business.datasource.network.main.responses.toRole
import com.templateapp.cloudapi.business.datasource.network.main.toTask
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS
import com.templateapp.cloudapi.presentation.main.task.list.TaskFilterOptions
import com.templateapp.cloudapi.presentation.main.task.list.TaskOrderOptions
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class CheckDeleteRole(
    private val service: OpenApiMainService,
    private val cache: AccountDao,
    private val serverMsgTranslator: ServerMsgTranslator
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        id: String,
    ):Flow<DataState<Response>>  = flow {
        emit(DataState.loading<Response>())
        if (authToken == null) {
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }

        // Update network
        val response = service.checkRole(
            authorization = authToken.token,
            id=id
        )

        if(response.response == "200"){
            emit(DataState.data<Response>(
                data = Response(
                    message = SuccessHandling.SUCCESS_200,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success(),
                    users = response.users.map { it.toAccount() }
                ),
                response = null
            ))
        }else if(response.response == "201"){
            emit(DataState.data<Response>(
                data = Response(
                    message = SuccessHandling.SUCCESS_201,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success(),
                    users = response.users.map { it.toAccount() }

                ),
                response = null,
            ))
        }else{
            throw Exception(ErrorHandling.ERROR_DELETING_ROLE)
        }

        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SuccessHandling.SUCCESS_PASSWORD_UPDATED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = null
        ))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}

