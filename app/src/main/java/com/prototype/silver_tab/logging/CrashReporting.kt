package com.prototype.silver_tab.logging

import android.content.Context
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Build
import android.os.Environment
import android.content.ContentValues
import java.io.OutputStream
import android.provider.MediaStore

class CrashReporting(private val context: Context) : Timber.Tree() {

    init {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            log(Log.ERROR, "UncaughtException", "App crash detectado!", throwable)
            System.exit(1)
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= Log.WARN) {
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val stackTrace = t?.stackTraceToString() ?: "Sem stack trace disponível."


            val color = when (priority) {
                Log.ERROR -> "\u001B[31m"  // Vermelho
                Log.WARN -> "\u001B[33m"   // Amarelo
                else -> "\u001B[0m"
            }

            val logMessage = """
                |$time [$tag]: $message
                |Stack Trace:
                |$stackTrace
                |\u001B[0m-------------------------------
            """.trimMargin()

            saveLogToFile(logMessage)
        }
    }

    fun saveLogToFile(logMessage: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "crash_logs_silver_tab.txt")
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/SilverTabLogs")
                }
                val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                uri?.let {
                    context.contentResolver.openOutputStream(it, "wa")?.use { output ->
                        output.write(logMessage.toByteArray())
                    }
                }
            } else {
                val logFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "crash_logs_silver_tab.txt")
                logFile.appendText(logMessage)
            }
        } catch (e: Exception) {
            Log.e("CrashReporting", "Erro ao salvar log no arquivo")
        }
    }




    fun getLogFile(): File {
        return File(context.filesDir, "crash_logs_silver_tab.txt")
    }

}
fun testarLog() {
    throw RuntimeException("Erro crítico de teste!")
}