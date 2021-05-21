package com.thesis.distanceguard.repository

/**
 * Created by Viet Hua on 05/20/2021.
 */
sealed class Result<out T : Any>
class Success<out T : Any>(val data: T) : Result<T>()
class Error(val exception: Throwable, val message: String = exception.localizedMessage) : Result<Nothing>()