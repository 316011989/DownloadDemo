package com.zyl.downloaddemo

class DownloadMessageEvent {
    private var downloadState = 0
    private var downloadPrograss = 0
    private var message: String? = null
    fun MessageEvent(message: String) {
        this.message = message
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }
}