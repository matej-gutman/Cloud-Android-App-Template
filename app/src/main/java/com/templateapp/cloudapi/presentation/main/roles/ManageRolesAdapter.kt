package com.templateapp.cloudapi.presentation.main.roles

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.*
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Device
import com.templateapp.cloudapi.business.domain.models.Role
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.BASE_URL
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.interactors.account.CheckDeleteRole
import com.templateapp.cloudapi.business.interactors.account.GetAllRoles
import com.templateapp.cloudapi.databinding.*
import com.templateapp.cloudapi.presentation.session.SessionManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ManageRolesAdapter(
    private val interaction: Interaction? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG: String = "AppDebug"

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Role>() {

        override fun areItemsTheSame(oldItem: Role, newItem: Role): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Role, newItem: Role): Boolean {
            return oldItem == newItem
        }

    }
    private val differ =
        AsyncListDiffer(
            AccountRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RoleViewHolder(
            LayoutRoleListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction = interaction,

        )
    }

    internal inner class AccountRecyclerChangeCallback(
        private val adapter: ManageRolesAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RoleViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(rolesList: List<Role>?, ){
        val newList = rolesList?.toMutableList()
        differ.submitList(newList)
    }

    class RoleViewHolder
    constructor(
        private val binding: LayoutRoleListItemBinding,
        private val interaction: Interaction?,

    ) : RecyclerView.ViewHolder(binding.root) {

        val state: MutableLiveData<ManageRolesState> = MutableLiveData(ManageRolesState())
        fun bind(item: Role) {
            binding.root.setOnClickListener {
            }

            binding.deleteButton.setOnClickListener {

                interaction?.checkRole(adapterPosition, item._id)
               // var modalcheck: ModalCheckBeforeDelete

            }


            binding.name.text = item.title
        }



    }


    interface Interaction {

        fun checkRole(position: Int, item: String)

    }
}
