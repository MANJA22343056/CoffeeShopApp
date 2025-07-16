package com.example.aplikasicoffeeshop.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import java.io.FileOutputStream

class ReceiptPrintAdapter(private val context: Context, private val content: String) : PrintDocumentAdapter() {

    private var pdfDocument: PrintedPdfDocument? = null
    private var totalPages = 1

    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: android.os.CancellationSignal,
        callback: LayoutResultCallback,
        extras: Bundle
    ) {
        pdfDocument = PrintedPdfDocument(context, newAttributes)
        if (cancellationSignal.isCanceled) {
            callback.onLayoutCancelled()
            return
        }

        val printInfo = PrintDocumentInfo.Builder("receipt.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(totalPages)
            .build()

        callback.onLayoutFinished(printInfo, true)
    }

    override fun onWrite(
        pages: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: android.os.CancellationSignal,
        callback: WriteResultCallback
    ) {
        val page = pdfDocument?.startPage(0)
        if (page == null) {
            callback.onWriteFailed("Gagal memuat halaman")
            return
        }

        val canvas = page.canvas
        val paint = Paint()
        paint.textSize = 12f
        val lines = content.split("\n")
        var yPos = 50f
        for (line in lines) {
            canvas.drawText(line, 50f, yPos, paint)
            yPos += 20f
        }

        pdfDocument?.finishPage(page)

        try {
            val fileOutputStream = FileOutputStream(destination.fileDescriptor)
            pdfDocument?.writeTo(fileOutputStream)
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback.onWriteFailed(e.message)
        } finally {
            pdfDocument?.close()
        }
    }

    override fun onFinish() {
        super.onFinish()
        pdfDocument?.close()
        pdfDocument = null
    }
}