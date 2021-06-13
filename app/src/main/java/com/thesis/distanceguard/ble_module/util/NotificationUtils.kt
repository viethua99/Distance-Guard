package com.thesis.distanceguard.ble_module.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.thesis.distanceguard.R
import com.thesis.distanceguard.ble_module.BLEController


object NotificationUtils {
     const val ACTION_TOO_CLOSE_NOTIFICATION =
        "com.thesis.distanceguard.ACTION_TOO_CLOSE_NOTIFICATION"

     const val ACTION_DANGER_NOTIFICATION =
        "com.thesis.distanceguard.ACTION_DANGER_NOTIFICATION"

    private const val TOO_CLOSE_CHANNEL_ID = "too_close_notification_channel"
    private const val DANGER_CHANNEL_ID = "danger_notification_channel"

    private const val NOTIFICATION_ID = 0

    private lateinit var context: Context
    private lateinit var notifyManager: NotificationManager



    fun init(context: Context) {
        this.context = context


        notifyManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {


            val notificationChannelTooClose = NotificationChannel(
                TOO_CLOSE_CHANNEL_ID,
                context.getString(R.string.notification_channel_name_too_close),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannelTooClose.enableLights(true)
            notificationChannelTooClose.lightColor = Color.RED
            notificationChannelTooClose.enableVibration(true)
            notificationChannelTooClose.description = context.getString(R.string.notification_channel_description_too_close)
            notifyManager.createNotificationChannel(notificationChannelTooClose)

            val notificationChannelDanger = NotificationChannel(
                DANGER_CHANNEL_ID,
                context.getString(R.string.notification_channel_name_danger),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannelDanger.enableLights(true)
            notificationChannelDanger.lightColor = Color.YELLOW
            notificationChannelDanger.enableVibration(true)
            notificationChannelDanger.description = context.getString(R.string.notification_channel_description_danger)
            notifyManager.createNotificationChannel(notificationChannelDanger)
        }


        BLEController.isStarted.observeForever { isStarted ->
            if (!isStarted) {
                notifyManager.cancelAll()
            }
        }
    }


    fun sendNotificationTooClose() {
        BLEController.isStarted.value?.let {
            if (it) {
                val updateIntent = Intent(ACTION_TOO_CLOSE_NOTIFICATION)
                val updatePendingIntent = PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT
                )

                val notifyBuilder = getNotificationBuilderTooClose()

                notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
            }
        }
    }


    fun sendNotificationDanger() {
        BLEController.isStarted.value?.let {
            if (it) {
                val notifyBuilder = getNotificationBuilderDanger()

                // Deliver the notification.
                notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
            }
        }
    }


    private fun getNotificationBuilderTooClose(): NotificationCompat.Builder {
        val notificationIntent = Intent(context, Class.forName("com.thesis.distanceguard.presentation.main.activity.MainActivity"))
        val notificationPendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_ID, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, TOO_CLOSE_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.too_close_notification_title))
            .setContentText(context.getString(R.string.too_close_notification_text))
            .setSmallIcon(R.drawable.ic_my_app)
            .setAutoCancel(true).setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)!!
    }



    private fun getNotificationBuilderDanger(): NotificationCompat.Builder {


        val notificationIntent = Intent(context, Class.forName("com.thesis.distanceguard.presentation.main.activity.MainActivity"))
        val notificationPendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_ID, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        return NotificationCompat.Builder(context, DANGER_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.danger_notification_title))
            .setContentText(context.getString(R.string.danger_notification_text))
            .setSmallIcon(R.drawable.ic_my_app)
            .setAutoCancel(true).setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)!!
    }

}