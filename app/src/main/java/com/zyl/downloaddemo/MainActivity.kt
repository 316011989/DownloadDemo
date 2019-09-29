package com.zyl.downloaddemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


/**
 * 触发下载的页面
 */
class MainActivity : AppCompatActivity() {

    private val starUrl1 =
        "http://116.198.2.5/dianying/89673/jixiantaopsheng_480.m3u8?stTime=1569755719&token=aa2d23b382f4ade66018b93899e268e8"
    private val starUrl2 =
        "http://116.198.2.5/dianying/90196/jefb001_480.m3u8?stTime=1569755739&token=ca22e7600d360b1481f2195ef5095160"
    private val starUrl3 =
        "http://oss.19051024.com/meiju/morigujian/48075/1/mrgj01.mkv"


    private val host = "v.1024renren.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )
        EventBus.getDefault().register(this)
        button.setOnClickListener {
            val downloadTask = DownloadEntity()
            downloadTask.downloadUrl = starUrl1
            downloadTask.host = host
            if (Uri.parse(downloadTask.downloadUrl).path.endsWith(".m3u8")) {
                //下载任务类型设置为m3u8
                downloadTask.type = DownloadEntity.typeM3u8
                //下载任务文件保存路径设置为id转为的md5命名的文件夹
                downloadTask.taskId = "179854"
                downloadTask.saveDir =
                    OtherUtils.getCacheDir() + OtherUtils.getCacheName(downloadTask.taskId)
                if (!File(downloadTask.saveDir).exists())
                    File(downloadTask.saveDir).mkdir()
                //读取解析m3u8文件,获取ts信息
                M3u8Reader(downloadTask).readM3u8()
            }

            val downloadTask1 = DownloadEntity()
            downloadTask1.downloadUrl = starUrl2
            downloadTask1.host = host
            if (Uri.parse(downloadTask1.downloadUrl).path.endsWith(".m3u8")) {
                //下载任务类型设置为m3u8
                downloadTask1.type = DownloadEntity.typeM3u8
                //下载任务文件保存路径设置为id转为的md5命名的文件夹
                downloadTask1.taskId = "179855"
                downloadTask1.saveDir =
                    OtherUtils.getCacheDir() + OtherUtils.getCacheName(downloadTask1.taskId)
                if (!File(downloadTask1.saveDir).exists())
                    File(downloadTask1.saveDir).mkdir()
                //读取解析m3u8文件,获取ts信息
                M3u8Reader(downloadTask1).readM3u8()
            }

            val mp4Queue = DownloadEntity()
            mp4Queue.type = DownloadEntity.typeMp4
            mp4Queue.downloadUrl = starUrl3
            mp4Queue.taskId = "179856"
            mp4Queue.saveDir =
                OtherUtils.getCacheDir() + OtherUtils.getCacheName(mp4Queue.taskId)
            if (!File(mp4Queue.saveDir).exists())
                File(mp4Queue.saveDir).mkdir()
            mp4Queue.tempName = "179856.tmp"
            mp4Queue.fileName = "179856.mkv"
            val task = SingleDownloadRunnable(mp4Queue, 0)
            DownloadCenter.instance.executor.execute(task)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    fun onMessageEvent(task: DownloadEntity) {
        when (task.downloadState) {
            DownloadEntity.state_prepare -> {
                when (task.taskId) {
                    "179854" -> {
                        text1.text = "任务准备"
                    }
                    "179855" -> {
                        text2.text = "任务准备"
                    }
                    "179856" -> {
                        text3.text = "任务准备"
                    }
                }

            }
            DownloadEntity.state_start -> {
                when (task.taskId) {
                    "179854" -> {
                        text1.text = "任务开始"
                    }
                    "179855" -> {
                        text2.text = "任务开始"
                    }
                    "179856" -> {
                        text3.text = "任务开始"
                    }
                }
                for (ts in task.tsList) {
                    val tsTask =
                        SingleDownloadRunnable(task, ts.tsIndex)
                    DownloadCenter.instance.executor.execute(tsTask)
                }
            }
            DownloadEntity.state_fail -> {
                when (task.taskId) {
                    "179854" -> {
                        text1.text = "任务失败"
                    }
                    "179855" -> {
                        text2.text = "任务失败"
                    }
                    "179856" -> {
                        text3.text = "任务失败"
                    }
                }
            }
            DownloadEntity.state_prograss -> {
                when (task.taskId) {
                    "179854" -> {
                        text1.text =
                            "任务${task.taskId}进度${task.tsSuccessCount}/${task.tsTotalCount}--${task.tsSuccessCount.toFloat() / task.tsTotalCount.toFloat() * 100}%"
                    }
                    "179855" -> {
                        text2.text =
                            "任务${task.taskId}进度${task.tsSuccessCount}/${task.tsTotalCount}--${task.tsSuccessCount.toFloat() / task.tsTotalCount.toFloat() * 100}%"
                    }
                    "179856" -> {
                        text3.text =
                            "任务${task.taskId}进度${task.loadedSize}/${task.fileSize}--${task.loadedSize.toFloat() / task.fileSize.toFloat() * 100}%"
                    }
                }
            }
            DownloadEntity.state_success -> {
                when (task.taskId) {
                    "179854" -> {
                        text1.text = "任务完成"
                    }
                    "179855" -> {
                        text2.text = "任务完成"
                    }
                    "179856" -> {
                        text3.text = "任务完成"
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun testOpenTaobao() {
        val uriString = "taobao://item.taobao.com/item.htm?id=576136559323"
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val uri = Uri.parse(uriString)
        intent.data = uri
        startActivityForResult(intent, 10001)
    }
}
