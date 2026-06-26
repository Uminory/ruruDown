package com.example.bilexport.core.util

/**
 * 统一的结果封装，用于表达成功/失败状态。
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val errorCode: com.example.bilexport.core.model.ExportErrorCode, val message: String = "") : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(code: com.example.bilexport.core.model.ExportErrorCode, message: String = ""): Result<Nothing> =
            Error(code, message)
    }
}