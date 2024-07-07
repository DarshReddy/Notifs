package com.app.notifs.helpers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.app.notifs.MainActivity
import com.app.notifs.R

class NotificationHelper(private val context: Context) {
    companion object {
        const val INCOMING_CALL_CHANNEL_ID = "incoming_calls"
        const val INCOMING_CALL_NOTIFICATION_ID = 101
        const val ACTION_ACCEPT_CALL = "accept_call"
        const val ACTION_REJECT_CALL = "reject_call"
        const val REQUEST_CODE_ACCEPT_CALL = 1001
        const val REQUEST_CODE_REJECT_CALL = 1002
        const val TITLE = "title"
        const val BODY = "body"
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        val channel = NotificationChannelCompat.Builder(
            INCOMING_CALL_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_MAX
        )
            .setName("Incoming calls")
            .setDescription("Incoming call alerts")
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(data: MutableMap<String, String>) {
        val notificationBuilder = NotificationCompat.Builder(
            context,
            INCOMING_CALL_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(data[TITLE])
            .setContentText(data[BODY])
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            .addAction(acceptCallAction(context))
            .addAction(rejectCallAction(context))
            .setOngoing(true)
            .setAutoCancel(true)

        val notification = notificationBuilder.build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(INCOMING_CALL_NOTIFICATION_ID, notification)
        }
    }

    private fun acceptCallAction(context: Context): NotificationCompat.Action {
        val intent = Intent(context, MainActivity::class.java).apply { action = ACTION_ACCEPT_CALL }
        val acceptCallIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_ACCEPT_CALL,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Action.Builder(
            IconCompat.createWithResource(context, R.drawable.baseline_call_24),
            "Accept",
            acceptCallIntent
        ).build()
    }

    private fun rejectCallAction(context: Context): NotificationCompat.Action {
        val intent = Intent(context, MainActivity::class.java).apply { action = ACTION_REJECT_CALL }
        val rejectCallIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_REJECT_CALL,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Action.Builder(
            IconCompat.createWithResource(context, R.drawable.baseline_call_end_24),
            "Decline",
            rejectCallIntent
        ).build()
    }

    fun cancelNotification() {
        notificationManager.cancel(INCOMING_CALL_NOTIFICATION_ID)
    }

}