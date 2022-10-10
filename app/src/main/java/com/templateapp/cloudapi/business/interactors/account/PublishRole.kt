package com.templateapp.cloudapi.business.interactors.account

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.cache.account.RoleDao
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.responses.toTask
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class PublishRole(
    private val service: OpenApiMainService,
    private val cache: RoleDao,
    private val serverMsgTranslator: ServerMsgTranslator
){
    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        title: String,
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt update
        val createResponse = service.addRole(
            authToken.token,
            title = title,
        )

        // If they don't have a paid membership account it will still return a 200 with failure message
        // Need to account for that

        if(createResponse.response == ErrorHandling.GENERIC_ERROR){

            throw Exception(ErrorHandling.ERROR_DELETING_ROLE)
        }

        // insert the new task into the cache
        cache.insert(createResponse.role.toEntity())

        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SuccessHandling.SUCCESS_ROLE_CREATED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = null,
        ))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}






















