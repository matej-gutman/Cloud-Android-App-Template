package com.templateapp.cloudapi.presentation.main.roles

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_FILTER_DATE_CREATED
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_FILTER_DATE_UPDATED
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_FILTER_USERNAME
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_ORDER_ASC
import com.templateapp.cloudapi.business.datasource.cache.task.TaskQueryUtils.Companion.TASK_ORDER_DESC
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Device
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.databinding.FragmentManageDevicesBinding
import com.templateapp.cloudapi.databinding.FragmentManageUsersBinding
import com.templateapp.cloudapi.databinding.FragmentTaskBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.main.account.users.detail.ViewAccountEvents
import com.templateapp.cloudapi.presentation.main.task.BaseTaskFragment
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents
import com.templateapp.cloudapi.presentation.util.TopSpacingItemDecoration
import com.templateapp.cloudapi.presentation.util.processQueue
import kotlinx.coroutines.*









class ManageRolesFragment : BaseAccountFragment(),
    ManageRolesAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

    private var recyclerAdapter: ManageRolesAdapter? = null // can leak memory so need to null
    private val viewModel: ManageRolesViewModel by viewModels()
    private lateinit var menu: Menu

    private var _binding: FragmentManageDevicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageDevicesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.swipeRefresh.setOnRefreshListener(this)
        initRecyclerView()
        subscribeObservers()
        setHasOptionsMenu(true)
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ManageRolesEvents.OnRemoveHeadFromQueue)
                    }
                })


            recyclerAdapter?.apply {
                submitList(rolesList = state.rolesList)

            }


            if(state.accountList != null && state.id!=""){
                dialogCheckRole(state.accountList, state.id)
            }
            if(state.done){
                Toast.makeText(context,"You have successfully deleted a role",Toast.LENGTH_LONG).show()
                setDoneToFalse()

            }

        })
    }


    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
        binding.focusableView.requestFocus()


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.manage_users_menu, this.menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.add -> {

                findNavController().navigate(R.id.action_manageRolesFragment_to_addRoleFragment)

                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }



    private  fun dialogCheckRole(list: List<Account> , id:String){


        val builder = AlertDialog.Builder(context)
        if(list.size != 0) {
            builder.setTitle("If you remove it, the following accounts will be disabled: ")
            var str: Array<String> = arrayOf()

            for (i in 0..list.size-1) {
                str += list[i].email
            }
            builder.setItems(str) { _, pos ->
                when (pos) {

                }
            }
        }
        else{
            builder.setTitle("Are you sure you want to delete this role?")
        }
       // builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Delete"){dialogInterface, which ->
         //   Toast.makeText(context,"clicked yes",Toast.LENGTH_LONG).show()
            delete(id)
        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which ->
           // Toast.makeText(context,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
        }
        //performing negative action
      /*  builder.setNegativeButton("No"){dialogInterface, which ->
            Toast.makeText(context,"clicked No",Toast.LENGTH_LONG).show()
        }*/
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()


    }



    private fun initRecyclerView(){
        binding.deviceRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@ManageRolesFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(10)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = ManageRolesAdapter(this@ManageRolesFragment )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){


                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "RolesFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(ManageRolesEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        _binding = null
    }


    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = false
    }

    override fun checkRole(position: Int, item: String) {

        try{
            viewModel.onTriggerEvent(
                ManageRolesEvents.CheckDeleteRole(
                    _id = item
                ))
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ManageRolesEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                ))
        }
    }
    fun delete(id: String) {

        println("tuuujhbj" + id)
        try{
            viewModel.onTriggerEvent(
                ManageRolesEvents.Delete(
                    _id = id
                ))
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ManageRolesEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                ))
        }
    }



    fun setDoneToFalse() {

        try{
            viewModel.onTriggerEvent(
                ManageRolesEvents.SetDoneToFalse
            )
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ManageRolesEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                ))
        }
    }
}








