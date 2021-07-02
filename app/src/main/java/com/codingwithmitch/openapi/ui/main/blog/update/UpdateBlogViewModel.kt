package com.codingwithmitch.openapi.ui.main.blog.update

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithmitch.openapi.interactors.blog.GetBlogFromCache
import com.codingwithmitch.openapi.interactors.blog.UpdateBlogPost
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.StateMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateBlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getBlogFromCache: GetBlogFromCache,
    private val updateBlogPost: UpdateBlogPost,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    val state: MutableLiveData<UpdateBlogState> = MutableLiveData()

    init {
        savedStateHandle.get<Int>("blogPostPk")?.let { blogPostPk ->
            onTriggerEvent(UpdateBlogEvents.getBlog(blogPostPk))
        }
    }

    fun onTriggerEvent(event: UpdateBlogEvents) {
        when(event){
            is UpdateBlogEvents.getBlog -> {
                getBlog(event.pk,)
            }
            is UpdateBlogEvents.OnUpdateUri -> {
                onUpdateImageUri(event.uri)
            }
            is UpdateBlogEvents.OnUpdateTitle -> {
                onUpdateTitle(event.title)
            }
            is UpdateBlogEvents.OnUpdateBody -> {
                onUpdateBody(event.body)
            }
            is UpdateBlogEvents.Update -> {
                update()
            }
            is UpdateBlogEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        // TODO
    }

    private fun onUpdateTitle(title: String){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                val curr = blogPost.copy(title = title)
                this.state.value = state.copy(blogPost = curr)
            }
        }
    }

    private fun onUpdateBody(body: String){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                val curr = blogPost.copy(body = body)
                this.state.value = state.copy(blogPost = curr)
            }
        }
    }

    private fun onUpdateImageUri(uri: Uri){
        state.value?.let { state ->
            this.state.value = state.copy(newImageUri = uri)
        }
    }

    private fun update(){
        state.value?.let { state ->
            state.blogPost?.let { blogPost ->
                val title = RequestBody.create(
                    MediaType.parse("text/plain"),
                    blogPost.title
                )
                val body = RequestBody.create(
                    MediaType.parse("text/plain"),
                    blogPost.body
                )
                var multipartBody: MultipartBody.Part? = null
                if(state.newImageUri != null){
                    state.newImageUri.path?.let { filePath ->
                        val imageFile = File(filePath)
                        if(imageFile.exists()){
                            val requestBody =
                                RequestBody.create(
                                    MediaType.parse("image/*"),
                                    imageFile
                                )
                            multipartBody = MultipartBody.Part.createFormData(
                                "image",
                                imageFile.name,
                                requestBody
                            )
                        }
                    }
                }
                updateBlogPost.execute(
                    authToken = sessionManager.state.value?.authToken,
                    slug = state.blogPost.slug,
                    title = title,
                    body = body,
                    image = multipartBody,
                ).onEach { dataState ->
                    this.state.value = state.copy(isLoading = dataState.isLoading)

                    dataState.data?.let { response ->
                        appendToMessageQueue( // Tell the UI it was updated
                            stateMessage = StateMessage(
                                response = response
                            )
                        )
                    }

                    dataState.stateMessage?.let { stateMessage ->
                        appendToMessageQueue(stateMessage)
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun getBlog(pk: Int){
        state.value?.let { state ->
            getBlogFromCache.execute(
                pk = pk
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { blogPost ->
                    this.state.value = state.copy(blogPost = blogPost)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }
}


















