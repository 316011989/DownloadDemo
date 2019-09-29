package com.zyl.downloaddemo

import android.os.Environment
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

class OtherUtils {

    companion object {

        fun getCacheDir(): String {
            return mkdirs(getAppDir() + "/Cache/")
        }

        fun getCacheName(name: String): String? {
            return md5Encode(name)
        }

        private fun getAppDir(): String {
            return mkdirs(Environment.getExternalStorageDirectory().toString() + "/DownloadDemo")
        }

        private fun mkdirs(dir: String): String {
            val file = File(dir)
            if (!file.exists()) {
                file.mkdirs()
            }
            return dir
        }

        fun md5Encode(str: String): String {
            try {
                val md = MessageDigest.getInstance("MD5")
                md.update(str.toByteArray())
                return BigInteger(1, md.digest()).toString(16)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return str
        }

    }


}