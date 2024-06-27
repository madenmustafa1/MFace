package com.maden.mface.common

import android.util.Log

internal object Logger {
    private const val _TAG = "MFace"

    fun execute(exception: Exception) {
        Log.e(_TAG, exception.stackTraceToString())
    }

    fun execute(message: String) {
        Log.d(_TAG, message)
    }
}

fun Exception.log() {
    Logger.execute(this)
}

fun String.errorLog() {
    Logger.execute(Exception(this))
}

fun String.log() {
    Logger.execute(this)
}