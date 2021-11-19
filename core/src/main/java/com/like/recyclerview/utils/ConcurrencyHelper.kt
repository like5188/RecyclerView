package com.like.recyclerview.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineStart.LAZY
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicReference

/**
 * 协程处理并发问题的工具类，确保在同一时间只会有一次请求进行。
 */
class ConcurrencyHelper {
    private val mutex = Mutex()
    private val activeTask = AtomicReference<Job?>(null)

    /**
     * 排队等待当前正在执行的任务完成后，再执行新任务。
     */
    suspend fun afterPrevious(block: suspend () -> Unit) {
        mutex.withLock {
            return block()
        }
    }

    /**
     * 如果有任务正在执行，则丢弃新任务。否则执行新任务。
     */
    suspend fun dropIfPreviousRunning(block: suspend () -> Unit) {
        if (activeTask.get() != null) {
            return
        }

        coroutineScope {
            val newTask = async(start = LAZY) {
                block()
            }
            newTask.invokeOnCompletion {
                activeTask.compareAndSet(newTask, null)
            }
            if (activeTask.compareAndSet(null, newTask)) {
                newTask.await()
            }
        }
    }

    /**
     * 先取消当前任务(如果有任务正在执行)，再执行新任务。
     */
    suspend fun cancelPreviousThenRun(block: suspend () -> Unit) {
        activeTask.get()?.cancelAndJoin()

        coroutineScope {
            val newTask = async(start = LAZY) {
                block()
            }
            newTask.invokeOnCompletion {
                activeTask.compareAndSet(newTask, null)
            }
            while (true) {
                if (activeTask.compareAndSet(null, newTask)) {
                    newTask.await()
                    break
                } else {
                    activeTask.get()?.cancelAndJoin()
                    yield()
                }
            }
        }
    }

}
