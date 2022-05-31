package com.templateapp.cloudapi.business.interactors.auth

import com.templateapp.cloudapi.business.datasource.network.auth.OpenApiAuthService
import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.auth.toEntity
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.templateapp.cloudapi.presentation.util.DataStoreKeys
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class Register(
    private val service: OpenApiAuthService,
    private val accountDao: AccountDao,
    private val authTokenDao: AuthTokenDao,
    private val appDataStoreManager: AppDataStore,
    private val serverMsgTranslator: ServerMsgTranslator
){
    fun execute(
        email: String,

    ): Flow<DataState<String>> = flow {
        emit(DataState.loading<String>())
        val registerResponse = service.register(
            email = email,

        )
        // Incorrect login credentials counts as a 200 response from server, so need to handle that
        registerResponse.error?.let{
            throw Exception(it)
        }

        // cache account information

        emit(DataState.data(data = "Success", response = null))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}














