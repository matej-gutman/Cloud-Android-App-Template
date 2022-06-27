package com.templateapp.cloudapi.presentation.main.report.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.templateapp.cloudapi.R
import com.templateapp.cloudapi.business.domain.models.Report
import com.templateapp.cloudapi.business.domain.models.Task
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.BASE_URL
import com.templateapp.cloudapi.business.domain.util.DateUtils
import com.templateapp.cloudapi.databinding.LayoutReportListItemBinding
import com.templateapp.cloudapi.databinding.LayoutTaskListItemBinding

class ReportListAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val requestOptions = RequestOptions
        .placeholderOf(R.drawable.default_image)
        .error(R.drawable.default_image)

    private val TAG: String = "AppDebug"

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Report>() {

        override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem == newItem
        }

    }
    private val differ =
        AsyncListDiffer(
            ReportRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReportViewHolder(
            LayoutReportListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            requestOptions = requestOptions,
            interaction = interaction,
        )
    }

    internal inner class ReportRecyclerChangeCallback(
        private val adapter: ReportListAdapter
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
            is ReportViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(tasksList: List<Report>?, ){
        val newList = tasksList?.toMutableList()
        differ.submitList(newList)
    }

    class ReportViewHolder
    constructor(
        private val binding: LayoutReportListItemBinding,
        private val requestOptions: RequestOptions,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Report) {
          /*  binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
*/
            Glide.with(binding.root)
                .setDefaultRequestOptions(requestOptions)
                .load(BASE_URL + item.image)
                .transition(withCrossFade())
                .into(binding.reportImage)
            binding.reportTitle.text = item.title
            binding.taskOwner.text = item.username
            binding.taskUpdateDate.text = DateUtils.convertLongToStringDate(item.updatedAt)
        }
    }

    interface Interaction {

       // fun onItemSelected(position: Int, item: Report)

    }
}
