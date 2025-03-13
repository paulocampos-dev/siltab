package com.prototype.silver_tab.logging

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CrashReporting class is responsible for handling uncaught exceptions and logging them.
 * It extends Timber.Tree to integrate with the app's logging system.
 */
@Singleton
class CrashReporting @Inject constructor(
    @ApplicationContext private val context: Context
) : Timber.Tree() {

    private val logFilename = "crash_logs_silver_tab.txt"
    private val logFilePath = Environment.DIRECTORY_DOCUMENTS + "/SilverTabLogs"
    private val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    init {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                // Log the crash with custom handler
                log(Log.ERROR, "UNCAUGHT_EXCEPTION", "App crash detected!", throwable)

                // Call the default handler after logging
                defaultUncaughtExceptionHandler?.uncaughtException(thread, throwable)
            } catch (e: Exception) {
                // If our crash handler crashes, make sure we still call the default handler
                Timber.e(e, "Error in custom crash handler")
                defaultUncaughtExceptionHandler?.uncaughtException(thread, throwable)
            } finally {
                // Force exit to prevent ANR
                System.exit(1)
            }
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= Log.WARN) {
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val stackTrace = t?.stackTraceToString() ?: "No stack trace available."
            val deviceInfo = getDeviceInfo()

            // Format log message
            val logMessage = """
                |======= ${getLogLevelString(priority)} =======
                |Time: $time
                |Tag: $tag
                |Message: $message
                |Device: $deviceInfo
                |
                |Stack Trace:
                |$stackTrace
                |======= END OF LOG =======
                |
            """.trimMargin()

            // Save log to file
            saveLogToFile(logMessage)
        }
    }

    /**
     * Saves a log message to a file, handling API level differences.
     */
    fun saveLogToFile(logMessage: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+ use MediaStore
                saveLogToMediaStore(logMessage)
            } else {
                // For older versions, use direct file access
                saveLogToExternalStorage(logMessage)
            }
        } catch (e: Exception) {
            Log.d("SLA", "Error saving log to file")
        }
    }

    /**
     * Get a human-readable log level string from the priority.
     */
    private fun getLogLevelString(priority: Int): String = when (priority) {
        Log.ERROR -> "ERROR"
        Log.WARN -> "WARNING"
        Log.INFO -> "INFO"
        Log.DEBUG -> "DEBUG"
        Log.VERBOSE -> "VERBOSE"
        else -> "UNKNOWN"
    }

    /**
     * Get basic device information for debugging.
     */
    private fun getDeviceInfo(): String {
        return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}), " +
                "Device: ${Build.MANUFACTURER} ${Build.MODEL}"
    }

    /**
     * Saves log using MediaStore for Android 10+.
     */
    private fun saveLogToMediaStore(logMessage: String) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, logFilename)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, logFilePath)
        }
        val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
        uri?.let {
            context.contentResolver.openOutputStream(it, "wa")?.use { output ->
                output.write(logMessage.toByteArray())
            }
        }
    }

    /**
     * Saves log to external storage for Android 9 and below.
     */
    private fun saveLogToExternalStorage(logMessage: String) {
        val logDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "SilverTabLogs")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }

        val logFile = File(logDir, logFilename)
        try {
            FileWriter(logFile, true).use { writer ->
                writer.append(logMessage)
            }
        } catch (e: IOException) {
            Timber.e(e, "Error writing to log file")
        }
    }

    /**
     * Get the log file.
     */
    fun getLogFile(): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "SilverTabLogs/$logFilename")
    }
}