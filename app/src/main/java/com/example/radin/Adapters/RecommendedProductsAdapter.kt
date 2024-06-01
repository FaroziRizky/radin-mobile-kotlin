package com.example.radin.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.radin.DetailProductPage

import com.example.radin.Models.ProductModel
import com.example.radin.R
import com.squareup.picasso.Picasso


class RecommendedProductsAdapter(private val products: List<ProductModel>) :
    RecyclerView.Adapter<RecommendedProductsAdapter.ViewHolder>() {

    // ViewHolder class to hold the view elements
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productName: TextView = view.findViewById(R.id.productName)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productStock: TextView = view.findViewById(R.id.productStock)
    }

    // Inflate the item layout and create the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recomended, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.productName
        holder.productPrice.text = "Rp ${product.price}"
        holder.productStock.text = product.stock.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailProductPage::class.java).apply {
                putExtra("idProduct", product.idProduct)
            }
            holder.itemView.context.startActivity(intent)

        }

        // Load image using Picasso or any other image loading library
        Picasso.get().load(product.picture).into(holder.productImage)
    }

    // Return the number of items in the list
    override fun getItemCount(): Int {
        return products.size
    }
}
