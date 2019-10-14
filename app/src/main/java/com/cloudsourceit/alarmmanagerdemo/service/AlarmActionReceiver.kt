package com.cloudsourceit.alarmmanagerdemo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cloudsourceit.alarmmanagerdemo.*
import com.cloudsourceit.alarmmanagerdemo.model.Alarm
import com.cloudsourceit.alarmmanagerdemo.model.AlarmInstance
import com.cloudsourceit.alarmmanagerdemo.utils.destroyNotification
import com.cloudsourceit.alarmmanagerdemo.utils.startNextAlarm
import java.util.*

class AlarmActionReceiver : BroadcastReceiver() {

    private val boxAlarm = ObjectBox.boxStore.boxFor(Alarm::class.java)
    private val boxAlarmInstance = ObjectBox.boxStore.boxFor(AlarmInstance::class.java)

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.extras?.run { getInt(KEY_ACTION) } ?: ACTION_CANCEL

        val alarmInstanceId = intent.extras?.run { getLong(KEY_INSTANCE_ID, 0) } ?: 0

        val alarmInstance = boxAlarmInstance.get(alarmInstanceId)

        alarmInstance?.run {
            boxAlarm.get(this.alarm.targetId)?.run {
                if(ACTION_CANCEL == action){
                    if(!repeatable){
                        boxAlarm.remove(id)
                        boxAlarmInstance.remove(alarmInstance)
                    }
                } else if(ACTION_SNOOZE == action){
                    val now = Calendar.getInstance()
                    now.add(Calendar.MINUTE, SNOOZE_TIME)
                    alarmInstance.time = now.timeInMillis
                    boxAlarmInstance.put(alarmInstance)
                }
            }
        }
        startNextAlarm(context)
        //Using instance id has notification id
        destroyNotification(context, alarmInstanceId.toInt())
    }
}