package com.example.bilexport.privileged.shizuku

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel

interface IBiliFileService : IInterface {

    fun listAvids(): MutableList<String>
    fun listSubDirs(avid: String): MutableList<String>
    fun readEntryJson(avid: String, subDir: String): String
    fun readCoverBytes(avid: String, subDir: String): ByteArray
    fun listQualityFolders(avid: String, subDir: String): MutableList<String>
    fun checkM4sFilesExist(avid: String, subDir: String, typeTag: String): Boolean
    fun copyM4sFiles(avid: String, subDir: String, typeTag: String, destDir: String): Int
    fun readDanmakuXml(avid: String, subDir: String): String?
    /** 检查 B站缓存根目录是否存在且可读 (shell UID) */
    fun checkBiliRootExists(): Boolean
    /** 批量同步封面: 返回封面数据列表（App 端自行写入缓存） */
    fun syncCoverBatch(requests: List<com.example.bilexport.core.model.CoverRequest>): List<com.example.bilexport.core.model.CoverData>

    abstract class Stub : Binder(), IBiliFileService {
        companion object {
            private const val DESCRIPTOR = "com.soyo.rurudown.privileged.shizuku.IBiliFileService"

            @JvmStatic
            fun asInterface(obj: IBinder?): IBiliFileService? {
                if (obj == null) return null
                val iin = obj.queryLocalInterface(DESCRIPTOR)
                if (iin != null && iin is IBiliFileService) return iin
                return Proxy(obj)
            }
        }

        override fun asBinder(): IBinder = this

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            when (code) {
                TRANSACTION_listAvids -> {
                    data.enforceInterface(DESCRIPTOR)
                    reply?.writeNoException()
                    reply?.writeStringList(listAvids())
                    return true
                }
                TRANSACTION_listSubDirs -> {
                    data.enforceInterface(DESCRIPTOR)
                    val avid = data.readString() ?: ""
                    reply?.writeNoException()
                    reply?.writeStringList(listSubDirs(avid))
                    return true
                }
                TRANSACTION_readEntryJson -> {
                    data.enforceInterface(DESCRIPTOR)
                    val avid = data.readString() ?: ""
                    val subDir = data.readString() ?: ""
                    reply?.writeNoException()
                    reply?.writeString(readEntryJson(avid, subDir))
                    return true
                }
                TRANSACTION_readCoverBytes -> {
                    data.enforceInterface(DESCRIPTOR)
                    val avid = data.readString() ?: ""
                    val subDir = data.readString() ?: ""
                    reply?.writeNoException()
                    reply?.writeByteArray(readCoverBytes(avid, subDir))
                    return true
                }
                TRANSACTION_listQualityFolders -> {
                    data.enforceInterface(DESCRIPTOR)
                    val avid = data.readString() ?: ""
                    val subDir = data.readString() ?: ""
                    reply?.writeNoException()
                    reply?.writeStringList(listQualityFolders(avid, subDir))
                    return true
                }
                TRANSACTION_checkM4sFilesExist -> {
                    data.enforceInterface(DESCRIPTOR)
                    val avid = data.readString() ?: ""
                    val subDir = data.readString() ?: ""
                    val typeTag = data.readString() ?: ""
                    reply?.writeNoException()
                    reply?.writeInt(if (checkM4sFilesExist(avid, subDir, typeTag)) 1 else 0)
                    return true
                }
                TRANSACTION_readDanmakuXml -> {
                    data.enforceInterface(DESCRIPTOR)
                    val avid = data.readString() ?: ""
                    val subDir = data.readString() ?: ""
                    val result = readDanmakuXml(avid, subDir)
                    reply?.writeNoException()
                    reply?.writeString(result)
                    return true
                }
                TRANSACTION_copyM4sFiles -> {
                    data.enforceInterface(DESCRIPTOR)
                    val avid = data.readString() ?: ""
                    val subDir = data.readString() ?: ""
                    val typeTag = data.readString() ?: ""
                    val destDir = data.readString() ?: ""
                    reply?.writeNoException()
                    reply?.writeInt(copyM4sFiles(avid, subDir, typeTag, destDir))
                    return true
                }
                TRANSACTION_syncCoverBatch -> {
                    data.enforceInterface(DESCRIPTOR)
                    val count = data.readInt()
                    val requests = mutableListOf<com.example.bilexport.core.model.CoverRequest>()
                    for (i in 0 until count) {
                        val avid = data.readString() ?: ""
                        val cid = data.readString() ?: ""
                        val subDir = data.readString() ?: ""
                        val destPath = data.readString() ?: ""
                        requests.add(com.example.bilexport.core.model.CoverRequest(avid, cid, subDir, destPath))
                    }
                    val result = syncCoverBatch(requests)
                    reply?.writeNoException()
                    reply?.writeTypedList(result)
                    return true
                }
                TRANSACTION_checkBiliRootExists -> {
                    data.enforceInterface(DESCRIPTOR)
                    val result = checkBiliRootExists()
                    reply?.writeNoException()
                    reply?.writeInt(if (result) 1 else 0)
                    return true
                }
                else -> return super.onTransact(code, data, reply, flags)
            }
        }

        private class Proxy(private val remote: IBinder) : IBiliFileService {
            override fun asBinder(): IBinder = remote

            override fun listAvids(): MutableList<String> {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    remote.transact(TRANSACTION_listAvids, data, reply, 0)
                    reply.readException()
                    return reply.createStringArrayList() ?: mutableListOf()
                } finally { data.recycle(); reply.recycle() }
            }

            override fun listSubDirs(avid: String): MutableList<String> {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeString(avid)
                    remote.transact(TRANSACTION_listSubDirs, data, reply, 0)
                    reply.readException()
                    return reply.createStringArrayList() ?: mutableListOf()
                } finally { data.recycle(); reply.recycle() }
            }

            override fun readEntryJson(avid: String, subDir: String): String {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeString(avid)
                    data.writeString(subDir)
                    remote.transact(TRANSACTION_readEntryJson, data, reply, 0)
                    reply.readException()
                    return reply.readString() ?: ""
                } finally { data.recycle(); reply.recycle() }
            }

            override fun readCoverBytes(avid: String, subDir: String): ByteArray {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeString(avid)
                    data.writeString(subDir)
                    remote.transact(TRANSACTION_readCoverBytes, data, reply, 0)
                    reply.readException()
                    return reply.createByteArray() ?: ByteArray(0)
                } finally { data.recycle(); reply.recycle() }
            }

            override fun listQualityFolders(avid: String, subDir: String): MutableList<String> {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeString(avid)
                    data.writeString(subDir)
                    remote.transact(TRANSACTION_listQualityFolders, data, reply, 0)
                    reply.readException()
                    return reply.createStringArrayList() ?: mutableListOf()
                } finally { data.recycle(); reply.recycle() }
            }

            override fun checkM4sFilesExist(avid: String, subDir: String, typeTag: String): Boolean {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeString(avid)
                    data.writeString(subDir)
                    data.writeString(typeTag)
                    remote.transact(TRANSACTION_checkM4sFilesExist, data, reply, 0)
                    reply.readException()
                    return reply.readInt() != 0
                } finally { data.recycle(); reply.recycle() }
            }

            override fun copyM4sFiles(avid: String, subDir: String, typeTag: String, destDir: String): Int {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeString(avid)
                    data.writeString(subDir)
                    data.writeString(typeTag)
                    data.writeString(destDir)
                    remote.transact(TRANSACTION_copyM4sFiles, data, reply, 0)
                    reply.readException()
                    return reply.readInt()
                } finally { data.recycle(); reply.recycle() }
            }

            override fun readDanmakuXml(avid: String, subDir: String): String? {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeString(avid)
                    data.writeString(subDir)
                    remote.transact(TRANSACTION_readDanmakuXml, data, reply, 0)
                    reply.readException()
                    val str = reply.readString()
                    return if (str.isNullOrEmpty()) null else str
                } finally { data.recycle(); reply.recycle() }
            }

            override fun syncCoverBatch(requests: List<com.example.bilexport.core.model.CoverRequest>): List<com.example.bilexport.core.model.CoverData> {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeInt(requests.size)
                    for (r in requests) {
                        data.writeString(r.avid)
                        data.writeString(r.cid)
                        data.writeString(r.subDir)
                        data.writeString(r.destPath)
                    }
                    remote.transact(TRANSACTION_syncCoverBatch, data, reply, 0)
                    reply.readException()
                    return reply.createTypedArrayList(com.example.bilexport.core.model.CoverData.CREATOR) ?: emptyList()
                } finally { data.recycle(); reply.recycle() }
            }

            override fun checkBiliRootExists(): Boolean {
                val data = Parcel.obtain(); val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    remote.transact(TRANSACTION_checkBiliRootExists, data, reply, 0)
                    reply.readException()
                    return reply.readInt() != 0
                } finally { data.recycle(); reply.recycle() }
            }
        }
    }

    companion object {
        const val TRANSACTION_listAvids = 1
        const val TRANSACTION_listSubDirs = 2
        const val TRANSACTION_readEntryJson = 3
        const val TRANSACTION_readCoverBytes = 4
        const val TRANSACTION_listQualityFolders = 5
        const val TRANSACTION_checkM4sFilesExist = 6
        const val TRANSACTION_copyM4sFiles = 7
        const val TRANSACTION_readDanmakuXml = 8
        const val TRANSACTION_syncCoverBatch = 9
        const val TRANSACTION_checkBiliRootExists = 10
    }
}
