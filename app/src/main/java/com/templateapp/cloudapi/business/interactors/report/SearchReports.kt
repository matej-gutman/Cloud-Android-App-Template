package com.templateapp.cloudapi.business.interactors.report

import com.templateapp.cloudapi.api.handleUseCaseException
import com.templateapp.cloudapi.business.datasource.cache.report.ReportDao
import com.templateapp.cloudapi.business.datasource.cache.report.toEntity
import com.templateapp.cloudapi.business.datasource.cache.report.toReport
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.network.main.toReport
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.models.Report
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.templateapp.cloudapi.business.domain.util.DataState
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.templateapp.cloudapi.business.domain.util.ErrorHandling.Companion.ERROR_TASK_DOES_NOT_EXIST
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.Response
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchReports(
    private val service: OpenApiMainService,
    private val cache: ReportDao,
    private val serverMsgTranslator: ServerMsgTranslator
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        page: Int,
    ): Flow<DataState<List<Report>>> = flow {
        emit(DataState.loading<List<Report>>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        try{ // catch network exception
            val reports = service.searchListReports(
                "${authToken.token}",
                limit = PAGINATION_PAGE_SIZE
            ).results.map { it.toReport() }


            // Insert into cache
            for(report in reports){
                try{
                    cache.insert(report.toEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            emit(
                DataState.error<List<Report>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        // load and check if tasks that are in cache are indeed present on the server
        var keepSearching = true;
        while(keepSearching){
            val cachedReports = cache.getAllReports(
                page = page
            )
            val cachedReportSize = cachedReports.size

            for(cachedReport in cachedReports){
                try { // try to load each task and check if it exists on the server
                    val serverReport = service.getReport(
                        "${authToken.token}",
                        id = cachedReport._id
                    )
                    // If task was not found on server, delete task from cache.
                    if(serverReport?.error?.contains(ERROR_TASK_DOES_NOT_EXIST) == true) {
                        cache.deleteReport(cachedReport._id)
                    }
                }catch (e: Exception){
                    emit(
                        DataState.error<List<Report>>(
                            response = Response(
                                message = "Unable to get the report from the server. Bad connection?",
                                uiComponentType = UIComponentType.None(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                }
            }
            // Stop searching once no tasks were deleted from the cache, as they all appear to be also on the server.
            if(cachedReportSize == cachedReports.size)
                keepSearching = false;
        }

        // Return cache to the caller
        val cachedReports = cache.getAllReports(
            page = page
        ).map { it.toReport() }

        emit(DataState.data(response = null, data = cachedReports))
    }.catch { e ->
        emit(handleUseCaseException(e, serverMsgTranslator))
    }
}



















