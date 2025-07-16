package com.example.aplikasicoffeeshop

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.print.PrintManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasicoffeeshop.adapter.ReceiptPrintAdapter
import com.example.aplikasicoffeeshop.model.Detail
import com.example.coffeeshop.DetailAdapter
import org.w3c.dom.Text
class DetailTransaksiActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var detailAdapter: DetailAdapter
    private lateinit var detailsList: List<Detail>
    private var totalHarga = 0
    private var pembayaran = 0
    private var kembalian = 0
    private var metodePembayaran: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaksi)

        // Ambil data dari intent
        totalHarga = intent.getIntExtra("total_price", 0)
        pembayaran = intent.getIntExtra("payment", 0)
        kembalian = intent.getIntExtra("change", 0)
        metodePembayaran = intent.getStringExtra("payment_method")
        detailsList = intent.getParcelableArrayListExtra<Detail>("details") ?: listOf()

        // Inisialisasi tampilan
        val textTotalHarga = findViewById<TextView>(R.id.textTotalHargaDetailTransaksi)
        val textPembayaran = findViewById<TextView>(R.id.textPembayaranDetailTransaksi)
        val textKembalian = findViewById<TextView>(R.id.textKembaianDetailTransaksi)
        val textMetodePembayaran = findViewById<TextView>(R.id.textMetodePembayaranDetailTransaksi)

        textTotalHarga.text = "Rp.$totalHarga"
        textPembayaran.text = "Rp.$pembayaran"
        textKembalian.text = "Rp.$kembalian"
        textMetodePembayaran.text = metodePembayaran

        recyclerView = findViewById(R.id.RecyclerViewDetailTransaksi)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        detailAdapter = DetailAdapter(detailsList)
        recyclerView.adapter = detailAdapter

        // Tombol cetak
        val buttonCetak = findViewById<Button>(R.id.buttonCetakTransaksi)
        buttonCetak.setOnClickListener {
            printReceipt()
        }
    }

    private fun printReceipt() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = ReceiptPrintAdapter(this, generateReceiptText())
        val jobName = "Receipt Print Job"
        printManager.print(jobName, printAdapter, null)
    }

    private fun generateReceiptText(): String {
        val receipt = StringBuilder()
        receipt.append("========== DETAIL TRANSAKSI ==========\n")
        receipt.append("Total Harga   : Rp.$totalHarga\n")
        receipt.append("Pembayaran    : Rp.$pembayaran\n")
        receipt.append("Kembalian     : Rp.$kembalian\n")
        receipt.append("Metode Bayar  : $metodePembayaran\n")
        receipt.append("\n--- Produk ---\n")
        for (detail in detailsList) {
            receipt.append("${detail.product_name} x${detail.quantity} = Rp.${detail.sub_total}\n")
        }
        receipt.append("======================================\n")
        return receipt.toString()
    }
}