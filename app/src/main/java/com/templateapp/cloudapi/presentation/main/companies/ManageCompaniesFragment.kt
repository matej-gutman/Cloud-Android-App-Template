package com.templateapp.cloudapi.presentation.main.companies

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
import com.templateapp.cloudapi.databinding.FragmentManageCompaniesBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.main.account.users.detail.ViewAccountEvents
import com.templateapp.cloudapi.presentation.main.task.BaseTaskFragment
import com.templateapp.cloudapi.presentation.main.task.list.TaskEvents
import com.templateapp.cloudapi.presentation.util.TopSpacingItemDecoration
import com.templateapp.cloudapi.presentation.util.processQueue
import kotlinx.coroutines.*









class ManageCompaniesFragment : BaseAccountFragment(),
    ManageCompaniesAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

    private var recyclerAdapter: ManageCompaniesAdapter? = null // can leak memory so need to null
    private val viewModel: ManageCompaniesViewModel by viewModels()
    private lateinit var menu: Menu

    private var _binding: FragmentManageCompaniesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageCompaniesBinding.inflate(layoutInflater)
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
                        viewModel.onTriggerEvent(ManageCompaniesEvents.OnRemoveHeadFromQueue)
                    }
                })


            recyclerAdapter?.apply {
                submitList(companiesList = state.companiesList)

            }


            if(state.accountList != null && state.id!=""){
                dialogCheckCompanies(state.accountList, state.id)
            }
            if(state.done){
                Toast.makeText(context,"You have successfully deleted a company",Toast.LENGTH_LONG).show()
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

                findNavController().navigate(R.id.action_manageCompaniesFragment_to_addCompanyFragment)

                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }



    private  fun dialogCheckCompanies(list: List<Account> , id:String){


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
            builder.setTitle("Are you sure you want to delete this company?")
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

        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()


    }



    private fun initRecyclerView(){
        binding.taskRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@ManageCompaniesFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(10)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = ManageCompaniesAdapter(this@ManageCompaniesFragment )
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
                        viewModel.onTriggerEvent(ManageCompaniesEvents.NextPage)
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

    override fun checkCompany(position: Int, item: String) {

        try{
            viewModel.onTriggerEvent(
                ManageCompaniesEvents.CheckDeleteCompany(
                    _id = item
                ))
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ManageCompaniesEvents.Error(
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

        try{
            viewModel.onTriggerEvent(
                ManageCompaniesEvents.Delete(
                    _id = id
                ))
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ManageCompaniesEvents.Error(
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
                ManageCompaniesEvents.SetDoneToFalse
            )
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ManageCompaniesEvents.Error(
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








