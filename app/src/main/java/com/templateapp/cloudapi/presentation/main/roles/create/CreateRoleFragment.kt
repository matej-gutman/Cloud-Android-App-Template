package com.templateapp.cloudapi.presentation.main.roles.create

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.util.*
import com.templateapp.cloudapi.databinding.FragmentCreateRoleBinding
import com.templateapp.cloudapi.databinding.FragmentCreateTaskBinding
import com.templateapp.cloudapi.presentation.auth.register.RegisterEvents
import com.templateapp.cloudapi.presentation.main.account.BaseAccountFragment
import com.templateapp.cloudapi.presentation.main.create_task.BaseCreateTaskFragment
import com.templateapp.cloudapi.presentation.main.create_task.CreateTaskEvents
import com.templateapp.cloudapi.presentation.main.roles.create.CreateRoleEvents
import com.templateapp.cloudapi.presentation.main.roles.create.CreateRoleViewModel
import com.templateapp.cloudapi.presentation.util.processQueue

class CreateRoleFragment : BaseAccountFragment() {


    private val viewModel: CreateRoleViewModel by viewModels()

    private var _binding: FragmentCreateRoleBinding? = null
    private val binding get() = _binding!!



    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateRoleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.addButton.setOnClickListener {

            add()
        }


        subscribeObservers()
    }


    private fun add() {

        cacheState()

        viewModel.onTriggerEvent(CreateRoleEvents.PublishRole(activity))

    }

    fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner, { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)
            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(CreateRoleEvents.OnRemoveHeadFromQueue)
                    }
                })
            setRoleProperties(
                title = state.title,
            )
            if (state.onPublishSuccess) {
                Toast.makeText(context,"You have successfully added new role",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setRoleProperties(
        title: String,
    ) {
        binding.inputTitle.setText(title)

    }

    private fun cacheState() {
        val title = binding.inputTitle.text.toString()
        viewModel.onTriggerEvent(CreateRoleEvents.OnUpdateTitle(title))

    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       // inflater.inflate(R.menu.create_menu, menu)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
       /* when (item.itemId) {
            R.id.create -> {
                cacheState()
                viewModel.onTriggerEvent(CreateTaskEvents.PublishTask(activity))
                return true
            }
        }*/
        return super.onOptionsItemSelected(item)
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}










