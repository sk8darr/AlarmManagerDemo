package com.cloudsourceit.alarmmanagerdemo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cloudsourceit.alarmmanagerdemo.ObjectBox
import com.cloudsourceit.alarmmanagerdemo.R
import com.cloudsourceit.alarmmanagerdemo.model.Alarm
import com.cloudsourceit.alarmmanagerdemo.model.AlarmInstance
import com.cloudsourceit.alarmmanagerdemo.model.AlarmInstance_
import com.cloudsourceit.alarmmanagerdemo.utils.showSimpleNotification
import com.cloudsourceit.alarmmanagerdemo.utils.startNextAlarm

/**
 * @author sk8 on 1/03/19.
 */
class AlarmServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val boxAlarm = ObjectBox.boxStore.boxFor(Alarm::class.java)
        val boxAlarmInstance = ObjectBox.boxStore.boxFor(AlarmInstance::class.java)

        val alarmId = intent.getLongExtra(KEY_ALARM, 0)
        val alarm = boxAlarm.get(alarmId)

        alarm?.run {

            if(!repeatable){
                boxAlarm.remove(this)
                boxAlarmInstance.remove(boxAlarmInstance.query().equal(AlarmInstance_.alarmId, this.id).build().find())
            }

            showSimpleNotification(context, alarm.title, alarm.content)
            startNextAlarm(context)
        }
    }

    companion object{
        const val KEY_ALARM = "alarm"
    }
}