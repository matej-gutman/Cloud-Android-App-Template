package com.templateapp.cloudapi.interactors.account

import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.interactors.account.GetAccount
import com.templateapp.cloudapi.datasource.cache.AccountDaoFake
import com.templateapp.cloudapi.datasource.cache.AppDatabaseFake
import com.templateapp.cloudapi.datasource.network.account.AccountResponses
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

/**
 * 1. Retrieve Account details from network and cache
 */
class AccountTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var getAccount: GetAccount

    // dependencies
    private lateinit var service: OpenApiMainService
    private lateinit var cache: AccountDaoFake

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        baseUrl = mockWebServer.url("/api/recipe/")
        service = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(OpenApiMainService::class.java)

        cache = AccountDaoFake(appDatabase)

        // instantiate the system in test
        getAccount = GetAccount(
            service = service,
            cache = cache,
        )
    }

    @Test
    fun getAccountSuccess() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.getAccountSuccess)
        )

        // User Information
        val id = AccountResponses.id
        val email = AccountResponses.email
        val username = AccountResponses.username
        val password = AccountResponses.password
        val token = AccountResponses.token

        // confirm no Account is stored in cache
        var cachedAccount = cache.searchByEmail(email)
        assert(cachedAccount == null)

        val emissions = getAccount.execute(
            authToken = AuthToken(
                accountId = id,
                token = token,
            )
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm Account is cached
        cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == email)
        assert(cachedAccount?.username == username)
        assert(cachedAccount?._id == id)

        // confirm second emission is the cached Account
        assert(emissions[1].data?.id == id)
        assert(emissions[1].data?.email == email)
        assert(emissions[1].data?.username == username)

        // loading done
        assert(!emissions[1].isLoading)
    }
}




















