package com.templateapp.cloudapi.presentation.main.roles

import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.util.Queue
import com.templateapp.cloudapi.business.domain.util.Response
import com.templateapp.cloudapi.business.domain.util.StateMessage

data class ManageRolesState(
    val isLoading: Boolean = false,
    val done: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
    val role: String = "",
    val id: String = "",
    val rolesList: List<Role> = listOf(),
    val accountList: List<Account>? = null,
    val response: Response? = null,
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
)
