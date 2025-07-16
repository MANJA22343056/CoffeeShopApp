package com.example.coffeeshop

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasicoffeeshop.R
import com.example.aplikasicoffeeshop.model.Detail

class DetailAdapter(private val detailList: List<Detail>) : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {

    class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.textNamaCoffeeItemDetailTransaksi)
        val quantity: TextView = itemView.findViewById(R.id.textJumlahItemDetailTransaksi)
        val subTotal: TextView = itemView.findViewById(R.id.textSubTotalItemDetailTransaksi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_produk, parent, false)
        return DetailViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val currentItem = detailList[position]
        holder.productName.text = currentItem.product_name
        holder.quantity.text = currentItem.quantity.toString()
        holder.subTotal.text = "Rp. " + currentItem.sub_total.toString()
    }

    override fun getItemCount() = detailList.size
}
