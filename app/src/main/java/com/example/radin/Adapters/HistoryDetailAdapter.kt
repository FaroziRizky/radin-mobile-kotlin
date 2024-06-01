package com.example.radin.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.radin.Models.ItemHistoryModel
import com.example.radin.R

class HistoryDetailAdapter(private val itemOrders: List<ItemHistoryModel>?) :
    RecyclerView.Adapter<HistoryDetailAdapter.MenuOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_order, parent, false)
        return MenuOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuOrderViewHolder, position: Int) {
        itemOrders?.let {
            val menuOrder = it[position]
            holder.itemName.text = menuOrder.name_product
            holder.itemVariant.text = menuOrder.type.toString()
            holder.itemPrice.text = "Rp ${formatPrice(menuOrder.price)}"
            holder.itemAmount.text = menuOrder.amount.toString()
        }
    }

    override fun getItemCount(): Int = itemOrders?.size ?: 0

    private fun formatPrice(price: Double): String {
        return String.format("%,.2f", price).replace(',', '.')
    }

    class MenuOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemVariant: TextView = itemView.findViewById(R.id.itemVariant)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val itemAmount: TextView = itemView.findViewById(R.id.itemAmount)
    }
}
