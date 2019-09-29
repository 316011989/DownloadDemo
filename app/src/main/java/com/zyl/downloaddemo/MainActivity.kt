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

    private val starUrl =
        "http://116.198.2.5/dianying/89673/jixiantaopsheng_720.m3u8?stTime=1569748907&token=b88707de72f81349379a12b1e349563f"

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
            downloadTask.downloadUrl = starUrl
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 5)
    fun onMessageEvent(event: DownloadEntity) {
        when (event.downloadState) {
            DownloadEntity.state_prepare -> {
                text.text = "任务准备"
            }
            DownloadEntity.state_start -> {
                text.text = "任务开始"
                for (ts in event.tsList) {
                    val tsTask =
                        SingleDownloadRunnable(DownloadCenter.priorityM3U8, event, ts.tsIndex)
                    DownloadCenter.instance.executor.execute(tsTask)
                }
            }
            DownloadEntity.state_fail -> {
                text.text = "任务失败"
            }
            DownloadEntity.state_prograss -> {
                text.text = "任务${event.taskId}进度${event.tsSuccessCount}"
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
