package com.example.android.coxcardealer.dealers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.coxcardealer.databinding.DealerViewItemBinding
import com.example.android.coxcardealer.network.Dealer

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class DealersListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Dealer,
        DealersListAdapter.DealerViewHolder>(DiffCallback) {

  /**
   * The [DealerViewHolder] constructor takes the binding variable from the associated
   * RecyclerViewItem, which nicely gives it access to the full [Dealer] information.
   */
  class DealerViewHolder(private var binding: DealerViewItemBinding) :
      RecyclerView.ViewHolder(binding.root) {
    fun bind(dealer: Dealer) {
      binding.dealer = dealer
      binding.executePendingBindings() // force immediate binding for correct view sizing
    }
  }

  // ** Determine which items have changed when the [List] of [Dealer]
  companion object DiffCallback : DiffUtil.ItemCallback<Dealer>() {
    override fun areItemsTheSame(oldItem: Dealer, newItem: Dealer): Boolean {
      return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Dealer, newItem: Dealer): Boolean {
      return oldItem.dealerId == newItem.dealerId
    }
  }

  // ** Create new [RecyclerView] item views (invoked by the Android Layout Manager)
  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): DealerViewHolder {
    return DealerViewHolder(DealerViewItemBinding
        .inflate(LayoutInflater.from(parent.context)))
  }

  // ** Replaces the contents of a view (invoked by the layout manager)
  override fun onBindViewHolder(holder: DealerViewHolder, position: Int) {
    val dealer = getItem(position)
    holder.itemView.setOnClickListener {
      onClickListener.onClick(dealer)
    }
    holder.bind(dealer)
  }

  /**
   * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Dealer]
   * associated with the selected item to the [onClick] function.
   * @param clickListener lambda that will be called with the selected [Dealer]
   */
  class OnClickListener(val clickListener: (dealer: Dealer) -> Unit) {
    fun onClick(dealer: Dealer) = clickListener(dealer)
  }
}

