package com.templateapp.cloudapi.interactors.task

import com.templateapp.cloudapi.business.datasource.network.main.OpenApiMainService
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.domain.util.MessageType
import com.templateapp.cloudapi.business.domain.util.UIComponentType
import com.templateapp.cloudapi.business.interactors.task.IsOwnerOfTask
import com.templateapp.cloudapi.datasource.cache.AppDatabaseFake
import com.templateapp.cloudapi.datasource.network.task.IsAuthorOfBlogPostResponses
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
 * 1. Success (They are the author)
 * 2. Success (They are not the author)
 * 3. Failure (AuthToken is null)
 * 4. Failure (Some random error)
 */
class IsAuthorOfTaskTest {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var isOwnerOfTask: IsOwnerOfTask

    // dependencies
    private lateinit var service: OpenApiMainService

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


        // instantiate the system in test
        isOwnerOfTask = IsOwnerOfTask(
            service = service,
        )
    }

    @Test
    fun isAuthorSuccess() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(IsAuthorOfBlogPostResponses.isAuthorSuccess)
        )

        // User Information
        val authToken = IsAuthorOfBlogPostResponses.authToken

        val blogPost = IsAuthorOfBlogPostResponses.blogPost

        // Execute use case
        val emissions = isOwnerOfTask.execute(
            authToken = authToken,
            id = blogPost.slug
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is boolean (true)
        assert(emissions[1].data == true)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun success_notTheAuthor() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(IsAuthorOfBlogPostResponses.isAuthorFail)
        )

        // User Information
        val authToken = IsAuthorOfBlogPostResponses.authToken

        val blogPost = IsAuthorOfBlogPostResponses.blogPost

        // Execute use case
        val emissions = isOwnerOfTask.execute(
            authToken = authToken,
            id = blogPost.slug
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is boolean with success message
        assert(emissions[1].data == false)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_EDIT_TASK_NEED_PERMISSION)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.None)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Success)


        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun failure_authTokenNull() = runBlocking {
        // User Information
        val authToken = null

        val blogPost = IsAuthorOfBlogPostResponses.blogPost

        // Execute use case
        val emissions = isOwnerOfTask.execute(
            authToken = authToken,
            id = blogPost.slug
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.Dialog)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun failure_randomError() = runBlocking {
        // condition the response
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(IsAuthorOfBlogPostResponses.isAuthorFail_randomError)
        )

        // User Information
        val authToken = IsAuthorOfBlogPostResponses.authToken

        val blogPost = IsAuthorOfBlogPostResponses.blogPost

        // Execute use case
        val emissions = isOwnerOfTask.execute(
            authToken = authToken,
            id = blogPost.slug
        ).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is boolean (false) with no error dialog
        assert(emissions[1].data == false)

        // loading done
        assert(!emissions[1].isLoading)
    }
}
















