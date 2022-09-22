package com.templateapp.cloudapi.presentation.main.devices

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.templateapp.cloudapi.business.domain.models.Device
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.databinding.FragmentManageDevicesBinding
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.util.TopSpacingItemDecoration
import com.templateapp.cloudapi.presentation.util.processQueue

class ManageDeviceFragment : BaseManageDevicesFragment(),
    ManageDeviceAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

    private var recyclerAdapter: ManageDeviceAdapter? = null // can leak memory so need to null
    private val viewModel: ManageDeviceViewModel by viewModels()
    private lateinit var menu: Menu

    private var _binding: FragmentManageDevicesBinding? = null
    private val binding get() = _binding!!

    private var isLoading = false

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
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner) { state ->

            isLoading = state.isLoading
            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ManageDevicesEvents.OnRemoveHeadFromQueue)
                    }
                })

            //binding.swipeRefresh.isRefreshing = state.isLoading

            recyclerAdapter?.apply {
                submitList(devicesList = state.deviceList)

            }
        }
    }

    private fun initRecyclerView(){
        binding.deviceRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@ManageDeviceFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(10)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = ManageDeviceAdapter(this@ManageDeviceFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                /* TODO: Perhaps remove this, since pagination is not needed? */
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged.")
                }
            })
            adapter = recyclerAdapter
        }
        binding.swipeRefresh.isRefreshing = false

        binding.swipeRefresh.setProgressViewOffset(false, -200, -200)

    }

    override fun onItemSelected(position: Int, item: Device) {

       /* try{
            viewModel.state.value?.let { state ->
                    val bundle = bundleOf("accountId" to item._id)
                    findNavController().navigate(R.id.action_manageUsersFragment_to_viewAccountFragment, bundle)
            }?: throw Exception("Null Task")
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                ManageDevicesEvents.Error(
                stateMessage = StateMessage(
                    response = Response(
                        message = e.message,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            ))
        }*/
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView.")
        super.onDestroyView()
        recyclerAdapter = null
        _binding = null
    }

    override fun onRefresh() {
        Log.d(TAG, "onRefresh.")
        binding.swipeRefresh.isRefreshing = false

        if(!isLoading)
            viewModel.onTriggerEvent(ManageDevicesEvents.GetDevice)

        /*viewModel.onTriggerEvent(
            ManageDevicesEvents.Error(
                stateMessage = StateMessage(
                    response = Response(
                        message = "Searching for devices...",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Info()
                    )
                )
            ))*/
    }

}
