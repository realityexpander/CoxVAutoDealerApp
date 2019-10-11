
package com.example.android.coxcardealer.vehicles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.coxcardealer.databinding.VehicleItemViewItemBinding
import com.example.android.coxcardealer.network.Vehicle

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class VehiclesListAdapter(private val onClickListener: OnClickListener ) :
    ListAdapter<Vehicle,
        VehiclesListAdapter.VehicleViewHolder>(DiffCallback) {

    /**
     * The DealerViewHolder constructor takes the binding variable from the associated
     * RecyclerViewItem, which nicely gives it access to the full [Dealer] information.
     */
    class VehicleViewHolder(private var binding: VehicleItemViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
            fun bind(vehicle: Vehicle) {
                binding.vehicle = vehicle
                binding.executePendingBindings() // force immediate binding for correct view sizing
            }
    }

    /**
     * Determine which items have changed when the [List] of [Dealer]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Vehicle>() {
        override fun areItemsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
            return oldItem.vehicleId == newItem.vehicleId
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the Android Layout Manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): VehicleViewHolder {
        return VehicleViewHolder(VehicleItemViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val dealer = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(dealer)
        }
        holder.bind(dealer)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Dealer]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Dealer]
     */
    class OnClickListener(val clickListener: (vehicle: Vehicle) -> Unit) {
        fun onClick(vehicle: Vehicle) = clickListener(vehicle)
    }
}

