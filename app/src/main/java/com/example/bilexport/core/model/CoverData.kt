package com.example.bilexport.core.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 封面数据——从 BiliFileService 返回的单个封面。
 */
data class CoverData(
    val id: String,        // "avid_cid"
    val bytes: ByteArray
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createByteArray() ?: ByteArray(0)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeByteArray(bytes)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CoverData> {
        override fun createFromParcel(parcel: Parcel): CoverData = CoverData(parcel)
        override fun newArray(size: Int): Array<CoverData?> = arrayOfNulls(size)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoverData) return false
        return id == other.id && bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = id.hashCode() * 31 + bytes.contentHashCode()
}
