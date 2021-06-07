package com.thesis.distanceguard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.thesis.distanceguard.R
import timber.log.Timber
import java.util.*

/**
 * Created by Viet Hua on 04/01/2021.
 */

class DistanceGuardService : Service() {
    companion object {
        const val TAG = "FireMeshService"
        const val NOTIFICATION_CHANNEL_ID = "ceslab.firemesh"

    }

    private var counter = 0

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            Timber.d("Larger")
            startMyOwnForeGround()
        } else {
            Timber.d("Smaller")
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_my_app)
                .setContentTitle("Distance Guard")
                .setContentText("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        stopTimerTask()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartService"
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeGround() {
        val channelName = "FireMesh Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_my_app)
            .setContentTitle("Fire Mesh")
            .setContentText("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    private var timer: Timer? = null
    private lateinit var timerTask: TimerTask

    fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                Timber.i("Count: --- ${counter++}")
            }
        }
        timer!!.schedule(timerTask, 1000, 1000)
    }

    fun stopTimerTask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

}