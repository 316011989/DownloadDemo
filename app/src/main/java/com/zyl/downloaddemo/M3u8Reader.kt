package com.zyl.downloaddemo

import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * m3u8下载
 * 读取ts片段,创建SingleTask任务
 * 提交给下载中心线程池
 */
class M3u8Reader(var task: DownloadEntity) {


    fun readM3u8() {
        task.downloadState = DownloadEntity.state_prepare
        EventBus.getDefault().post(task)
        connectM3u8File(task.downloadUrl).execute()
    }

    /**
     * 读取m3u8内容,生成m3u8下载任务实体类
     */
    private fun readBuffered(
        reader: BufferedReader
    ) {
        val basepath = task.downloadUrl.substring(0, task.downloadUrl.lastIndexOf("/") + 1)
        task.tsList = mutableListOf()
        task.tsTotalCount = 0
        var ts: DownloadEntity.TS = DownloadEntity.TS()
        reader.forEachLine {
            var line = it
            if (line.startsWith("#")) {
                if (line.startsWith("#EXTINF:") && line.endsWith(",")) {
                    ts = DownloadEntity.TS()
                    line = line.substring(8, line.length - 1)
                    ts.tsDuration = line
                }
            } else {
                if (line.endsWith("m3u8")) {
                    connectM3u8File(basepath + line)
                    return@forEachLine
                }
                task.tsTotalCount++
                ts.tsUrl = basepath + line
                ts.tsIndex = task.tsTotalCount - 1
                ts.tempPath = task.saveDir + File.separator + ts.fileName() + "tmp"
                ts.savePath = task.saveDir + File.separator + ts.fileName()
                task.tsList.add(ts)
            }
        }
        reader.close()
    }

    /**
     * 请求m3u8文件
     */
    inner class connectM3u8File(
        private val url: String
    ) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val conn = URL(url).openConnection() as HttpURLConnection
            if (!TextUtils.isEmpty(task.host))
                conn.addRequestProperty("host", task.host)
            return if (conn.responseCode == 200) {
                readBuffered(BufferedReader(InputStreamReader(conn.inputStream)))
                true
            } else {
                Log.e("M3u8Reader", "m3u8下载失败,此处处理失败回调")
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            if (result) {
                task.downloadState = DownloadEntity.state_start
                EventBus.getDefault().post(task)
            } else {
                task.downloadState = DownloadEntity.state_fail
                EventBus.getDefault().post(task)
            }
        }

    }
}