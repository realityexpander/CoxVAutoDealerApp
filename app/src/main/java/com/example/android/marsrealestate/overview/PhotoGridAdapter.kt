
package com.example.android.marsrealestate.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.Dealer
import com.example.android.marsrealestate.network.MarsProperty

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */

// ** delete
class PhotoGridAdapter( private val onClickListener: OnClickListener ) :
        ListAdapter<MarsProperty,
                PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback) {
//class PhotoGridAdapter( private val onClickListener: OnClickListener ) :
//    ListAdapter<Dealer,
//        PhotoGridAdapter.DealerViewHolder>(DiffCallback) {

    /**
     * The MarsPropertyViewHolder constructor takes the binding variable from the associated
     * GridViewItem, which nicely gives it access to the full [Dealer] information.
     */
    // ** delete
    class MarsPropertyViewHolder(private var binding: GridViewItemBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(marsProperty: MarsProperty) {
            binding.property = marsProperty
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }
//    class DealerViewHolder(private var binding: GridViewItemBinding):
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(dealer: Dealer) {
//            binding.dealer = dealer
//            // This is important, because it forces the data binding to execute immediately,
//            // which allows the RecyclerView to make the correct view size measurements
//            binding.executePendingBindings()
//        }
//    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Dealer]
     * has been updated.
     */
    // ** delete
    companion object DiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
        override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }
//    companion object DiffCallback : DiffUtil.ItemCallback<Dealer>() {
//        override fun areItemsTheSame(oldItem: Dealer, newItem: Dealer): Boolean {
//            return oldItem === newItem
//        }
//
//        override fun areContentsTheSame(oldItem: Dealer, newItem: Dealer): Boolean {
//            return oldItem.dealerId == newItem.dealerId
//        }
//    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    //** delete
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MarsPropertyViewHolder {
        return MarsPropertyViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }
//    override fun onCreateViewHolder(parent: ViewGroup,
//                                    viewType: Int): DealerViewHolder {
//        return DealerViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
//    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: MarsPropertyViewHolder, position: Int) {
        val marsProperty = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(marsProperty)
        }
        holder.bind(marsProperty)
    }
//    override fun onBindViewHolder(holder: DealerViewHolder, position: Int) {
//        val dealer = getItem(position)
//        holder.itemView.setOnClickListener {
//            onClickListener.onClick(dealer)
//        }
//        holder.bind(dealer)
//    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Dealer]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Dealer]
     */
    class OnClickListener(val clickListener: (marsProperty:MarsProperty) -> Unit) {
        fun onClick(marsProperty:MarsProperty) = clickListener(marsProperty)
    }
//    class OnClickListener(val clickListener: (dealer: Dealer) -> Unit) {
//        fun onClick(dealer: Dealer) = clickListener(dealer)
//    }
}

