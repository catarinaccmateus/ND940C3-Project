package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var loadURL: String = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager
    private var downloadStatus = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            if (loadURL.isEmpty()) {
                Toast.makeText(this, R.string.toast_content, Toast.LENGTH_SHORT).show()
                custom_button.buttonState = ButtonState.Disabled
            } else {
                if (isOnline(this)) {
                    custom_button.buttonState = ButtonState.Loading
                    try {
                        download()
                    } catch(e: Exception) {
                        Log.w("MainActivity", e.toString())
                        Toast.makeText(this, "There was an error loading. Try again.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    custom_button.buttonState = ButtonState.Completed
                    Toast.makeText(this, "Check internet connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

        options_container.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                custom_button.buttonState = ButtonState.Completed
                val radioButton: RadioButton = options_container.findViewById(checkedId)
                val index = options_container.indexOfChild(radioButton)
                when (index) {
                    0 -> loadURL = GLIDE_URL
                    1 -> loadURL = LOAD_APP_URL
                    2 -> loadURL = RETROFIT_URL
                    else -> loadURL = ""
                }
            }
        })
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_button.buttonState = ButtonState.Completed

            if (id != null) {
                val query = DownloadManager.Query()
                    .setFilterById(id)

                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    val status: Int =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    cursor.close()

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadStatus = "Succeeded"
                    } else {
                        downloadStatus = "Failed"
                    }
                }

                }


        }
    }


    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(loadURL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
