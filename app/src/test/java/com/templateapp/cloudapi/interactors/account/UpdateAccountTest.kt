package com.templateapp.cloudapi.interactors.account

import com.templateapp.cloudapi.business.datasource.cache.account.toEntity
import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.AuthToken
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.SuccessHandling
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.interactors.account.UpdateAccount
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
 * 1. Update account success
 * 2. Update account failure (email already in use)
 * 3. Update account failure (username already in use)
 * 4. Update account failure ("something went wrong")
 * 5. Update account failure (Random malformed unknown error is returned)
 */
class UpdateAccountTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var updateAccount: UpdateAccount

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
        updateAccount = UpdateAccount(
            service = service,
            cache = cache,
        )
    }

    @Test
    fun updateAccountSuccess() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updateAccountSuccess)
        )

        // User Information
        val id = AccountResponses.id
        val updatedEmail = AccountResponses.email
        val updatedUsername = AccountResponses.username
        val password = AccountResponses.password
        val token = AccountResponses.token

        // make sure an account already exists in the cache
        val initialEmail = "someEmail@gmail.com"
        val initialUsername = "someusername"
        val account = Account(
            id = id,
            email = initialEmail,
            username = initialUsername
        )
        cache.insertAndReplace(account.toEntity())

        // confirm Account is cached
        var cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        val emissions = updateAccount.execute(
            authToken = AuthToken(
                accountId = id,
                token = token,
            ),
            _id = id,
            email = updatedEmail,
            name = updatedUsername,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm Account is updated in the cache
        cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == updatedEmail)
        assert(cachedAccount?.username == updatedUsername)
        assert(cachedAccount?._id == id)

        // confirm second emission is a success response
        assert(emissions[1].data?.message == SuccessHandling.SUCCESS_ACCOUNT_UPDATED)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateAccountFail_emailInUse() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updateAccountFail_emailInUse)
        )

        // User Information
        val id = AccountResponses.id
        val updatedEmail = AccountResponses.email
        val updatedUsername = AccountResponses.username
        val password = AccountResponses.password
        val token = AccountResponses.token

        // make sure an account already exists in the cache
        val initialEmail = "someEmail@gmail.com"
        val initialUsername = "someusername"
        val account = Account(
            id = id,
            email = initialEmail,
            username = initialUsername
        )
        cache.insertAndReplace(account.toEntity())

        // confirm Account is cached
        var cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        val emissions = updateAccount.execute(
            authToken = AuthToken(
                accountId = id,
                token = token,
            ),
            _id = id,
            email = updatedEmail,
            name = updatedUsername,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm Account is NOT updated in the cache
        cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_EMAIL_IN_USE)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateAccountFail_usernameInUse() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updateAccountFail_usernameInUse)
        )

        // User Information
        val id = AccountResponses.id
        val updatedEmail = AccountResponses.email
        val updatedUsername = AccountResponses.username
        val password = AccountResponses.password
        val token = AccountResponses.token

        // make sure an account already exists in the cache
        val initialEmail = "someEmail@gmail.com"
        val initialUsername = "someusername"
        val account = Account(
            id = id,
            email = initialEmail,
            username = initialUsername
        )
        cache.insertAndReplace(account.toEntity())

        // confirm Account is cached
        var cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        val emissions = updateAccount.execute(
            authToken = AuthToken(
                accountId = id,
                token = token,
            ),
            _id = id,
            email = updatedEmail,
            name = updatedUsername,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm Account is NOT updated in the cache
        cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_USERNAME_IN_USE)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateAccountFail() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updateAccountFail)
        )

        // User Information
        val id = AccountResponses.id
        val updatedEmail = AccountResponses.email
        val updatedUsername = AccountResponses.username
        val password = AccountResponses.password
        val token = AccountResponses.token

        // make sure an account already exists in the cache
        val initialEmail = "someEmail@gmail.com"
        val initialUsername = "someusername"
        val account = Account(
            id = id,
            email = initialEmail,
            username = initialUsername
        )
        cache.insertAndReplace(account.toEntity())

        // confirm Account is cached
        var cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        val emissions = updateAccount.execute(
            authToken = AuthToken(
                accountId = id,
                token = token,
            ),
            _id = id,
            email = updatedEmail,
            name = updatedUsername,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm Account is NOT updated in the cache
        cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_SOMETHING_WENT_WRONG)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun updateAccountFail_Random() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(AccountResponses.updateAccountFail_Random)
        )

        // User Information
        val id = AccountResponses.id
        val updatedEmail = AccountResponses.email
        val updatedUsername = AccountResponses.username
        val password = AccountResponses.password
        val token = AccountResponses.token

        // make sure an account already exists in the cache
        val initialEmail = "someEmail@gmail.com"
        val initialUsername = "someusername"
        val account = Account(
            id = id,
            email = initialEmail,
            username = initialUsername
        )
        cache.insertAndReplace(account.toEntity())

        // confirm Account is cached
        var cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        val emissions = updateAccount.execute(
            authToken = AuthToken(
                accountId = id,
                token = token,
            ),
            _id = id,
            email = updatedEmail,
            name = updatedUsername,
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm Account is NOT updated in the cache
        cachedAccount = cache.searchByPk(id)
        assert(cachedAccount?.email == initialEmail)
        assert(cachedAccount?.username == initialUsername)
        assert(cachedAccount?._id == id)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_UPDATE_ACCOUNT)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }
}


























