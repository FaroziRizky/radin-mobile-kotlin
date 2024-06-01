package com.example.radin.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.radin.ApiService.ApiService
import com.example.radin.ApiService.TokenManager
import com.example.radin.Models.CartItem
import com.example.radin.R
import com.example.radin.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutAdapter(
    private val cartItems: MutableList<CartItem>,

) : RecyclerView.Adapter<CheckoutAdapter.CartViewHolder>() {


    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartItemImage: ImageView = view.findViewById(R.id.cartItemImage)
        val cartItemName: TextView = view.findViewById(R.id.cartItemName)
        val cartItemVariant: TextView = view.findViewById(R.id.cartItemVariant)
        val cartItemAmount: TextView = view.findViewById(R.id.cartItemAmount)
        val cartItemPrice: TextView = view.findViewById(R.id.cartItemPrice)
        val stock: TextView = view.findViewById(R.id.stock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        when(cartItem.type){
            1-> holder.cartItemVariant.text ="Sembako"
            2-> holder.cartItemVariant.text ="Daging"
            3-> holder.cartItemVariant.text ="Buah"
        }

        holder.stock.text = cartItem.stock.toString()
        holder.cartItemName.text = cartItem.product_name
        holder.cartItemPrice.text = "Rp. ${cartItem.price}"
        holder.cartItemAmount.text = cartItem.amount.toString()

        Glide.with(holder.itemView).load(cartItem.picture).into(holder.cartItemImage)

    }

    override fun getItemCount(): Int = cartItems.size
}
