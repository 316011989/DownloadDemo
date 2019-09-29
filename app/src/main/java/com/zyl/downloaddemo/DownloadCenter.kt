package com.zyl.downloaddemo

import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 * create by zyl
 *
 * 下载中心
 * 线程池
 */
class DownloadCenter {

    //在PriorityBlockingQueue传入比较优先级的规则,10代表初始队列容量,理论是无界的(除非OOM)
    var queue: PriorityBlockingQueue<Runnable> =
        PriorityBlockingQueue(10, ComparePriority())
    //创建线程池,线程池可执行线程数量3,线程池可创建数量6,超过6的线程进入queue队列
    var executor: ThreadPoolExecutor = ThreadPoolExecutor(3, 6, 100, TimeUnit.SECONDS, queue)

    companion object {
        /**
         * 双重校验锁式单例
         */
        val instance: DownloadCenter by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DownloadCenter()
        }

    }


    /**
     *
     */
    inner class ComparePriority<T : Runnable> : Comparator<T> {

        override fun compare(lhs: T, rhs: T): Int {
            return (lhs as SingleDownloadRunnable).index - (rhs as SingleDownloadRunnable).index
        }
    }

}