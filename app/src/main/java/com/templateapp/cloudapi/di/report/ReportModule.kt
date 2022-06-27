package com.templateapp.cloudapi.di.report

import com.templateapp.cloudapi.business.datasource.cache.report.ReportDao
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.interactors.report.PublishReport
import com.templateapp.cloudapi.business.interactors.report.SearchReports
import com.templateapp.cloudapi.business.interactors.task.*
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportModule {



    @Singleton
    @Provides
    fun provideSearchReports(
        service: OpenApiMainService,
        dao: ReportDao,
        serverMsgTranslator: ServerMsgTranslator
    ): SearchReports {
        return SearchReports(service, dao, serverMsgTranslator)
    }


    @Singleton
    @Provides
    fun providePublishReport(
        service: OpenApiMainService,
        dao: ReportDao,
        serverMsgTranslator: ServerMsgTranslator
    ): PublishReport {
        return PublishReport(service, dao, serverMsgTranslator)
    }

}

















