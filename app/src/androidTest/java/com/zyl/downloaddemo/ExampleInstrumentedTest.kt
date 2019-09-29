package com.zyl.downloaddemo

import android.net.Uri
import androidx.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun testMethond() {
        val downloadUrl =
            "http://py4dgpl53.bkt.clouddn.com/dianshiju/56432/xixingji02/xixingji02_720.m3u8"
        val uriSplit = Uri.parse(downloadUrl)
        println("scheme---${uriSplit.scheme}")
        println("authority---${uriSplit.authority}")
        println("host---${uriSplit.host}")
        println("fragment---${uriSplit.fragment}")
        println("query---${uriSplit.query}")
        println("path---${uriSplit.path}")
        println("port---${uriSplit.port}")
        println("userInfo---${uriSplit.userInfo}")
    }
}
