package com.cloudsourceit.alarmmanagerdemo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cloudsourceit.alarmmanagerdemo.*
import com.cloudsourceit.alarmmanagerdemo.service.AlarmActionReceiver


@Suppress("unused")
fun showSimpleNotification(context: Context, title: String, content: String, id: Int = -1) {

    val notificationId = if(-1 != id) id else NOTIFICATION_ID
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(notificationId , builder.build())
}

fun showNotificationWithSnoozeAction(context: Context, title: String, content: String, alarmInstanceId: Long, id: Int = -1){
    val notificationId = if(-1 != id) id else NOTIFICATION_ID
    val intent = Intent(context, AlarmActionReceiver::class.java)
        .putExtra(KEY_INSTANCE_ID, alarmInstanceId)
        .putExtra(KEY_ACTION, ACTION_CANCEL)
    val intentSnooze = Intent(context, AlarmActionReceiver::class.java)
        .putExtra(KEY_INSTANCE_ID, alarmInstanceId)
        .putExtra(KEY_ACTION, ACTION_SNOOZE)

    val pendingIntentCancel = PendingIntent.getBroadcast(context, notificationId * NOTIFICATION_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    val pendingIntentSnooze = PendingIntent.getBroadcast(context, notificationId, intentSnooze, PendingIntent.FLAG_CANCEL_CURRENT)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)
        .addAction(R.drawable.ic_snooze_black_24dp, context.getString(R.string.snooze), pendingIntentSnooze)
        .addAction(R.drawable.ic_cancel_24px, context.getString(R.string.cancel), pendingIntentCancel)

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(notificationId , builder.build())
}

fun destroyNotification(context: Context, notificationId: Int){
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.cancel(notificationId)
}

fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.channel_name)
        val description = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager?.createNotificationChannel(channel)
    }
}
