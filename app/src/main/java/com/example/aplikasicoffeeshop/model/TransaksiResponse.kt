package com.example.aplikasicoffeeshop.model

import android.os.Parcel
import android.os.Parcelable

data class TransaksiResponse(
    val id: Int,
    val total_price: Int,
    val payment: Int,
    val change: Int,
    val payment_method: String,
    val created_at: String,
    val details: List<Detail>
)

data class Detail(
    val id: Int,
    val product_name: String,
    val quantity: Int,
    val sub_total: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(product_name)
        parcel.writeInt(quantity)
        parcel.writeInt(sub_total)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Detail> {
        override fun createFromParcel(parcel: Parcel): Detail {
            return Detail(parcel)
        }

        override fun newArray(size: Int): Array<Detail?> {
            return arrayOfNulls(size)
        }
    }
}