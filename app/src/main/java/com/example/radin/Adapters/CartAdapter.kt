package com.example.radin.Adapters

import android.content.Context
import android.content.Intent
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
import com.example.radin.DetailProductPage
import com.example.radin.Models.CartItem
import com.example.radin.R
import com.example.radin.api.RetrofitInstance
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val amountChangeListener: OnAmountChangeListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnAmountChangeListener {
        fun onAmountChanged()
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartItemImage: ImageView = view.findViewById(R.id.cartItemImage)
        val cartItemName: TextView = view.findViewById(R.id.cartItemName)
        val cartItemVariant: TextView = view.findViewById(R.id.cartItemVariant)
        val cartItemAmount: TextView = view.findViewById(R.id.cartItemAmount)
        val cartItemPrice: TextView = view.findViewById(R.id.cartItemPrice)
        val stock: TextView = view.findViewById(R.id.stock)
        val decreaseAmount: ImageButton = view.findViewById(R.id.decreaseAmount)
        val increaseAmount: ImageButton = view.findViewById(R.id.increaseAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        when (cartItem.type) {
            1 -> holder.cartItemVariant.text = "Sembako"
            2 -> holder.cartItemVariant.text = "Daging"
            3 -> holder.cartItemVariant.text = "Buah"
        }

        holder.cartItemName.text = cartItem.product_name
        holder.stock.text = cartItem.stock.toString()
        holder.cartItemPrice.text = "Rp. ${cartItem.price}"
        holder.cartItemAmount.text = cartItem.amount.toString()
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailProductPage::class.java).apply {
                putExtra("idProduct", cartItem.id_product)
            }
            holder.itemView.context.startActivity(intent)
        }

        Glide.with(holder.itemView).load(cartItem.picture).into(holder.cartItemImage)

        holder.decreaseAmount.setOnClickListener {
            if (cartItem.amount > 1) {
                updateCartItemAmount(holder.itemView.context, cartItem.id_cart_item, -1) {
                    if (it) {
                        cartItem.amount -= 1
                        holder.cartItemAmount.text = cartItem.amount.toString()
                        amountChangeListener.onAmountChanged()
                    }
                }
            } else {
                showDeleteConfirmationDialog(holder.itemView.context, position)
            }
        }

        holder.increaseAmount.setOnClickListener {
            updateCartItemAmount(holder.itemView.context, cartItem.id_cart_item, 1) {
                if (it) {
                    cartItem.amount += 1
                    holder.cartItemAmount.text = cartItem.amount.toString()
                    amountChangeListener.onAmountChanged()
                }
            }
        }
    }

    private fun updateCartItemAmount(
        context: Context,
        idCartItem: Int,
        deltaAmount: Int,
        callback: (Boolean) -> Unit
    ) {
        val token = TokenManager.getToken(context)
        val requestBody = mapOf("id_cart_item" to idCartItem, "amount" to deltaAmount)
        if (token != null) {
            RetrofitInstance.api.setCartItemAmount(token, requestBody)
                .enqueue(object : Callback<ApiService.setCartItemAmountResponse> {
                    override fun onResponse(
                        call: Call<ApiService.setCartItemAmountResponse>,
                        response: Response<ApiService.setCartItemAmountResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.status == 200) {
                            Log.d("CartAdapter", "onResponse: ${response.body()}")
                            callback(true)
                        } else {
                            if (response.code() == 400) {
                                // Handle 400 Bad Request error
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = JSONObject(errorBody).getString("message")
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            } else {
                                Log.d("ErrorCuy", "addToCart: ${response.code()}")
                                Toast.makeText(
                                    context,
                                    response.body()?.message ?: "Gagal menambahkan jumlah produk",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.setCartItemAmountResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
                        callback(false)
                    }
                })
        } else {
            callback(false)
        }
    }

    private fun showDeleteConfirmationDialog(context: Context, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus menu ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                val idCartItem = cartItems[position].id_cart_item
                updateCartItemAmount(context, idCartItem, -1) { success ->
                    if (success) {
                        cartItems.removeAt(position)
                        notifyItemRemoved(position)
                        amountChangeListener.onAmountChanged()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun getItemCount(): Int = cartItems.size
}
