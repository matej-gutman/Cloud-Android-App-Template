package com.templateapp.cloudapi.presentation.main.companies

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
import com.templateapp.cloudapi.business.domain.models.*
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.BASE_URL
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.templateapp.cloudapi.business.domain.util.ErrorHandling
import com.templateapp.cloudapi.business.interactors.account.CheckDeleteRole
import com.templateapp.cloudapi.business.interactors.account.GetAllRoles
import com.templateapp.cloudapi.databinding.*
import com.templateapp.cloudapi.presentation.session.SessionManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ManageCompaniesAdapter(
    private val interaction: Interaction? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG: String = "AppDebug"

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Company>() {

        override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
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
            LayoutCompanyListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction = interaction,

        )
    }

    internal inner class AccountRecyclerChangeCallback(
        private val adapter: ManageCompaniesAdapter
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

    fun submitList(companiesList: List<Company>?, ){
        val newList = companiesList?.toMutableList()
        differ.submitList(newList)
    }

    class RoleViewHolder
    constructor(
        private val binding: LayoutCompanyListItemBinding,
        private val interaction: Interaction?,

    ) : RecyclerView.ViewHolder(binding.root) {

        val state: MutableLiveData<ManageCompaniesState> = MutableLiveData(ManageCompaniesState())
        fun bind(item: Company) {
            binding.root.setOnClickListener {
            }

            binding.deleteButton.setOnClickListener {

                interaction?.checkCompany(adapterPosition, item._id)
               // var modalcheck: ModalCheckBeforeDelete

            }


            binding.name.text = item.title
        }



    }


    interface Interaction {

        fun checkCompany(position: Int, item: String)

    }
}
