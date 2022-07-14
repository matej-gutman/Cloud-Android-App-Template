package com.templateapp.cloudapi.presentation.main.report.list

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
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
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.databinding.FragmentReportBinding
import com.templateapp.cloudapi.presentation.main.report.BaseReportFragment
import com.templateapp.cloudapi.presentation.main.task.BaseTaskFragment
import com.templateapp.cloudapi.presentation.util.TopSpacingItemDecoration
import com.templateapp.cloudapi.presentation.util.processQueue
import kotlinx.coroutines.*

class ReportFragment : BaseReportFragment(),
    ReportListAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

    private var recyclerAdapter: ReportListAdapter? = null // can leak memory so need to null
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var menu: Menu

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        binding.swipeRefresh.setOnRefreshListener(this)
        initRecyclerView()
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ReportEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(reportList = state.reportList)
            }
        })
    }

    private fun initRecyclerView(){
        binding.taskRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@ReportFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = ReportListAdapter(this@ReportFragment)
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
                        Log.d(TAG, "TaskFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(ReportEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.manage_users_menu, this.menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.add -> {

                findNavController().navigate(R.id.action_reportFragment_to_addReportFragment)

                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onRefresh() {
        viewModel.onTriggerEvent(ReportEvents.NewSearch)
        binding.swipeRefresh.isRefreshing = false
    }



    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        _binding = null
    }
}








