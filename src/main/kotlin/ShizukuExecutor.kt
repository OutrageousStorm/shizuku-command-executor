package com.outrageousstorm.shizuku

import android.content.Context
import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import dev.rikka.shizuku.Shizuku
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Execute shell commands via Shizuku (elevated ADB access)
 * No root needed — works with adb connect on rooted devices or Shizuku service
 */
class ShizukuExecutor(context: Context) {
    init {
        Shizuku.addBinderReceivedListener(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        requestPermission()
    }

    private val binderReceivedListener = Runnable {
        // Shizuku service is available
    }

    private val binderDeadListener = Runnable {
        // Shizuku service died
    }

    private fun requestPermission() {
        if (Shizuku.isPreV11()) {
            return
        }
        if (Shizuku.checkSelfPermission() == 0) {
            return
        }
        Shizuku.requestPermission(0)
    }

    /**
     * Execute a shell command and return output
     * @param cmd The shell command to execute
     * @return Command output as string
     */
    suspend fun exec(cmd: String): String = withContext(Dispatchers.IO) {
        try {
            val shellService = ShellService.getInstance()
            shellService.exec(cmd)
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    /**
     * Check if Shizuku permission is granted
     */
    fun hasPermission(): Boolean {
        return Shizuku.checkSelfPermission() == 0
    }

    /**
     * Execute multiple commands in sequence
     */
    suspend fun execBatch(commands: List<String>): List<String> = withContext(Dispatchers.IO) {
        commands.map { exec(it) }
    }
}

object ShellService {
    private var shellService: IShellService? = null

    fun getInstance(): IShellService {
        if (shellService == null) {
            val binder = Shizuku.getBinder()
            shellService = IShellService.Stub.asInterface(binder)
        }
        return shellService!!
    }
}

interface IShellService : IInterface {
    @Throws(Exception::class)
    fun exec(cmd: String): String

    abstract class Stub(private val binder: IBinder) : IInterface {
        companion object {
            fun asInterface(binder: IBinder?): IShellService? {
                if (binder == null) return null
                return object : IShellService {
                    override fun asBinder() = binder
                    override fun exec(cmd: String): String {
                        val parcel = Parcel.obtain()
                        val reply = Parcel.obtain()
                        return try {
                            parcel.writeInterfaceToken("com.outrageousstorm.IShellService")
                            parcel.writeString(cmd)
                            binder.transact(1, parcel, reply, 0)
                            reply.readException()
                            reply.readString() ?: ""
                        } finally {
                            parcel.recycle()
                            reply.recycle()
                        }
                    }
                }
            }
        }

        override fun asBinder() = binder
    }

    fun asBinder(): IBinder
}
