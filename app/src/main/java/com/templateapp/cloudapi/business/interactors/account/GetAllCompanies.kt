package com.templateapp.cloudapi.business.interactors.account

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.cache.account.*
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.toAccount
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.auth.toEntity
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.returnOrderedTaskQuery
import com.templateapp.cloudapi.business.datasource.cache.task.toEntity
import com.templateapp.cloudapi.business.datasource.cache.task.toTask
import com.templateapp.cloudapi.business.datasource.network.main.responses.toCompany
import com.templateapp.cloudapi.business.datasource.network.main.responses.toList
import com.templateapp.cloudapi.business.datasource.network.main.responses.toRole
import com.templateapp.cloudapi.business.datasource.network.main.toTask
import com.templateapp.cloudapi.business.domain.models.*
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

class GetAllCompanies(
    private val service: OpenApiMainService,
    private val cache: CompanyDao,
    private val serverMsgTranslator: ServerMsgTranslator
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
    ): Flow<DataState<List<Company>>> = flow {
        emit(DataState.loading<List<Company>>())
        if (authToken == null) {
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // get Tasks from network
        try { // catch network exception

            val companies = service.getAllCompanies(
                "${authToken.token}",
            ).companies.map { it.toCompany() }

            // Insert into cache
           for (company in companies) {
                try {
                    cache.insert(company.toEntity())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val cachedCompanies= cache.getAllCompanies().map { it.toCompany() }


            emit(DataState.data(response = null, data = cachedCompanies))

        } catch (e: Exception) {
            emit(
                DataState.error<List<Company>>(
                    response = Response(
                        message = "Unable get all companies.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
            // load and check if tasks that are in cache are indeed present on the server


        }
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}


