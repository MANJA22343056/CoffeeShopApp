package com.example.aplikasicoffeeshop.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasicoffeeshop.DetailTransaksiActivity
import com.example.aplikasicoffeeshop.R
import com.example.aplikasicoffeeshop.model.TransaksiResponse
import java.text.SimpleDateFormat
import java.util.Locale

class TransaksiAdapter(private val transaksiList: List<TransaksiResponse>) : RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {
    private lateinit var transaksiListener: clickListener
    interface clickListener {
        fun onItemClick(position: Int)
    }
    fun setOnClickListener(listener: clickListener){
        transaksiListener = listener
    }

    class TransaksiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val totalPrice: TextView = itemView.findViewById(R.id.textItemTotalHargaTransaksi)
        val payment: TextView = itemView.findViewById(R.id.textItemPembayaranTransaksi)
        val change: TextView =itemView.findViewById(R.id.textItemKembalianTransaksi)
        val date: TextView = itemView.findViewById(R.id.textItemTanggalTransaksi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaksi, parent, false)
        return TransaksiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        val currentItem = transaksiList[position]
        holder.totalPrice.text = currentItem.total_price.toString()
        holder.payment.text = currentItem.payment.toString()
        holder.change.text = currentItem.change.toString()

        val formattedDate = formatDateString(currentItem.created_at)
        holder.date.text = formattedDate

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailTransaksiActivity::class.java)
            intent.putExtra("total_price", currentItem.total_price)
            intent.putExtra("payment", currentItem.payment)
            intent.putExtra("change", currentItem.change)
            intent.putExtra("payment_method", currentItem.payment_method)
            intent.putParcelableArrayListExtra("details", ArrayList(currentItem.details))
            context.startActivity(intent)
        }
    }


    override fun getItemCount() = transaksiList.size
    private fun formatDateString(dateString: String): String {
        return try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val targetFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
            val date = originalFormat.parse(dateString)
            targetFormat.format(date)
        } catch (e: Exception) {
            dateString // Jika terjadi kesalahan, kembalikan string asli
        }
    }
}