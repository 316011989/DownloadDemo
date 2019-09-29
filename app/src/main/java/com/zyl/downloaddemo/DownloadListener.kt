package com.zyl.downloaddemo

interface DownloadListener {
    fun onDownloadSuccess(task: DownloadEntity, size: Long)

    fun onDownloadPause(task: DownloadEntity)

    fun onDownloadProgress(task: DownloadEntity)

    fun onDownloadPrepare(task: DownloadEntity)

    fun onDownloadError(task: DownloadEntity, errorMsg: String)
}