package com.example.aplikasicoffeeshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasicoffeeshop.R
import com.example.aplikasicoffeeshop.model.Produk

class ProdukAdapter(private val produkList: List<Produk>) : RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder>() {
    private lateinit var produkListener: clickListener
    interface clickListener {
        fun onItemClick(position: Int)
        fun onTambahStokClick(position: Int)
    }
    fun setOnClickListener(listener: clickListener){
        produkListener = listener
    }

    class ProdukViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView=itemView.findViewById(R.id.textItemNamaCoffee)
        val price: TextView=itemView.findViewById(R.id.textItemHargaCoffee)
        val stock: TextView=itemView.findViewById(R.id.textItemStokCoffee)
        val btnTambahStok: TextView=itemView.findViewById(R.id.buttonTambahStokProduk)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return ProdukViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val currentItem = produkList[position]
        holder.name.text = currentItem.name
        holder.price.text = currentItem.price.toString()
        holder.stock.text = currentItem.stock.toString()

        holder.btnTambahStok.setOnClickListener {
            produkListener.onTambahStokClick(position)
        }

    }

    override fun getItemCount() = produkList.size
}