package com.templateapp.cloudapi.di.companies

import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.account.CompanyDao
import com.templateapp.cloudapi.business.datasource.cache.account.RoleDao
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.interactors.account.*
import com.templateapp.cloudapi.business.interactors.companies.CheckDeleteCompany
import com.templateapp.cloudapi.business.interactors.companies.DeleteCompany
import com.templateapp.cloudapi.business.interactors.companies.PublishCompany
import com.templateapp.cloudapi.business.interactors.task.SearchTasks
import com.templateapp.cloudapi.presentation.util.ServerMsgTranslator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CompanyModule {


    @Singleton
    @Provides
    fun provideCheckCompanies(
        service: OpenApiMainService,
        cache: AccountDao,
        serverMsgTranslator: ServerMsgTranslator
    ): CheckDeleteCompany {
        return CheckDeleteCompany(service, cache, serverMsgTranslator)
    }


    @Singleton
    @Provides
    fun provideDeleteCompany(
        service: OpenApiMainService,
        cache: CompanyDao,
        serverMsgTranslator: ServerMsgTranslator
    ): DeleteCompany {
        return DeleteCompany(service, cache, serverMsgTranslator)
    }

    @Singleton
    @Provides
    fun provideCreateCompany(
        service: OpenApiMainService,
        cache: CompanyDao,
        serverMsgTranslator: ServerMsgTranslator
    ): PublishCompany {
        return PublishCompany(service, cache, serverMsgTranslator)
    }

}










