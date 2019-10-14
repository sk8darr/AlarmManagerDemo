package com.cloudsourceit.alarmmanagerdemo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cloudsourceit.alarmmanagerdemo.KEY_INSTANCE_ID
import com.cloudsourceit.alarmmanagerdemo.ObjectBox
import com.cloudsourceit.alarmmanagerdemo.model.Alarm
import com.cloudsourceit.alarmmanagerdemo.model.AlarmInstance
import com.cloudsourceit.alarmmanagerdemo.utils.showNotificationWithSnoozeAction
import com.cloudsourceit.alarmmanagerdemo.utils.startNextAlarm

/**
 * @author sk8 on 1/03/19.
 */
class AlarmServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val boxAlarm = ObjectBox.boxStore.boxFor(Alarm::class.java)
        val boxAlarmInstance = ObjectBox.boxStore.boxFor(AlarmInstance::class.java)

        val alarmInstanceId = intent.getLongExtra(KEY_INSTANCE_ID, 0L)
        val alarmInstance = boxAlarmInstance.get(alarmInstanceId)

        alarmInstance?.run {
            boxAlarm.get(this.alarm.targetId)?.also {
                //Using instance id has notification id
                showNotificationWithSnoozeAction(context, it.title, it.content, this.instanceId, this.instanceId.toInt() )
                startNextAlarm(context)
            }
        }
    }
}