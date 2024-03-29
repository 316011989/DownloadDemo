package com.zyl.downloaddemo

import android.text.TextUtils
import android.util.Log
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * 单个下载线程
 * 用于下载ts或mp4文件
 */
class SingleDownloadRunnable(
    var task: DownloadEntity,
    var index: Int
) : Runnable {

    override fun run() {
        if (task.type == DownloadEntity.typeM3u8) {
            downloadTs()
        } else {
            downloadMp4()
        }
    }

    private fun downloadMp4() {
        Log.e(
            "SingleDownloadRunnable",
            "task url===${task.downloadUrl}"
        )
        val url = URL(task.downloadUrl)
        val conn = url.openConnection() as HttpURLConnection
        if (!TextUtils.isEmpty(task.host))
            conn.addRequestProperty("host", task.host)
        conn.connect()
        task.fileSize = conn.contentLength.toLong()
        task.downloadState = DownloadEntity.state_start
        EventBus.getDefault().post(task)
        val buf = ByteArray(2048)
        val fos: FileOutputStream?
        val inputStream: InputStream = conn.inputStream
        val tmpFile = File(task.saveDir + "/" + task.tempName)
        if (!tmpFile.exists()) {
            tmpFile.createNewFile()
        }
        fos = FileOutputStream(tmpFile)
        var current = 0L
        var len: Int
        var flag = true
        while (flag) {
            len = inputStream.read(buf)
            flag = len != -1
            if (flag) {
                fos.write(buf, 0, len)
                current += len.toLong()
            }
            task.loadedSize = current
            task.downloadState = DownloadEntity.state_prograss
            EventBus.getDefault().post(task)
        }
        fos.flush()
        conn.disconnect()
        tmpFile.renameTo(File(task.saveDir + "/" + task.fileName))
        task.downloadState = DownloadEntity.state_success
        EventBus.getDefault().post(task)
    }

    private fun downloadTs() {
        val ts = task.tsList[index]
        Log.e(
            "SingleDownloadRunnable",
            "task url===${ts.tsUrl}"
        )
        if (!File(ts.savePath).exists()) {
            val url = URL(ts.tsUrl)
            val conn = url.openConnection() as HttpURLConnection
            if (!TextUtils.isEmpty(task.host))
                conn.addRequestProperty("host", task.host)
            conn.connect()
            val buf = ByteArray(2048)
            val fos: FileOutputStream?
            val inputStream: InputStream = conn.inputStream
            val tmpFile = File(ts.tempPath) //临时文件
            if (!tmpFile.exists()) {
                tmpFile.createNewFile()
            }
            fos = FileOutputStream(tmpFile)
            var current = 0L
            var len: Int
            var flag = true
            while (flag) {
                len = inputStream.read(buf)
                flag = len != -1
                if (flag) {
                    fos.write(buf, 0, len)
                    current += len.toLong()
                }
            }
            fos.flush()
            conn.disconnect()
            tmpFile.renameTo(File(ts.savePath))
        }
        task.downloadState = DownloadEntity.state_prograss
        task.tsSuccessCount++
        EventBus.getDefault().post(task)
    }
}