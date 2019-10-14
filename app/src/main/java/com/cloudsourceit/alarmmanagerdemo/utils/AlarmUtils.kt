package com.cloudsourceit.alarmmanagerdemo.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import com.cloudsourceit.alarmmanagerdemo.ObjectBox
import com.cloudsourceit.alarmmanagerdemo.model.Alarm
import com.cloudsourceit.alarmmanagerdemo.model.AlarmInstance
import com.cloudsourceit.alarmmanagerdemo.model.AlarmInstance_
import com.cloudsourceit.alarmmanagerdemo.model.Alarm_
import com.cloudsourceit.alarmmanagerdemo.service.AlarmServiceReceiver
import java.lang.Exception
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.collections.ArrayList

const val KEY_ALARM_ID = "alarm_id"
private const val REQUEST_CODE = 1002
private val boxAlarm = ObjectBox.boxStore.boxFor(Alarm::class.java)
private val boxAlarmInstance = ObjectBox.boxStore.boxFor(AlarmInstance::class.java)


fun startNextAlarm(context: Context){
    val now = Calendar.getInstance()
    val instances = boxAlarmInstance.query().order(AlarmInstance_.time).build().find()
    for(instance in instances){
        if(now.timeInMillis < instance.time){
            setAlarmAtTime(context, instance)
            break
        }
    }
}

fun setAlarmAtTime(context: Context, alarmInstance: AlarmInstance){
    val alarmService = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
    cancelAlarm(context, alarmService, REQUEST_CODE)
    val intent = Intent(context, AlarmServiceReceiver::class.java).putExtra(AlarmServiceReceiver.KEY_ALARM, alarmInstance.alarm.targetId)
    val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0)
    alarmService.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        alarmInstance.time,
        pendingIntent
    )
    updateInstance(alarmInstance)
}

private fun cancelAlarm(context: Context, alarmService: AlarmManager, requestCode: Int) {
    val intent = Intent(context, AlarmServiceReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
    alarmService.cancel(pendingIntent)
    pendingIntent.cancel()
}

/**
 * @param orgCalendar the calendar used for set the new date
 * @param dayIndex the index of required day. Use a range from 1 to 7
 */
fun calculateNextDate(orgCalendar: Calendar, @IntRange(from = 1, to = 7) dayIndex: Int): Calendar{
    require(!(dayIndex > 7 || dayIndex < 1)) { "Day of week is out of range" }

    val now = Calendar.getInstance()
    //Use local calendar to prevent original date change
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = orgCalendar.timeInMillis
    calendar.set(Calendar.DAY_OF_WEEK, dayIndex)
    if(now.after(calendar)){
        calendar.add(Calendar.DAY_OF_WEEK, 7)
    }
    return calendar
}

fun getAllAlarms(): MutableList<Alarm>{
    return boxAlarm.query().build().find()
}

fun getAllInstances(alarmId: Long): MutableList<AlarmInstance>{
    return boxAlarmInstance.query().equal(AlarmInstance_.alarmId, alarmId).build().find()
}

fun getAlarmById(id: Long): Alarm?{
    return boxAlarm.query().equal(Alarm_.id, id).build().findFirst()
}

fun saveAlarmAndInstances(alarm: Alarm, alarmInstanceList: ArrayList<AlarmInstance>){
    boxAlarm.put(alarm)
    boxAlarmInstance.put(alarmInstanceList)
}

fun updateInstance(instance: AlarmInstance){
    calculateNextDate(instance.time.toCalendar(), instance.weekDay)
    boxAlarmInstance.put(instance)
}

fun deleteAllInstancesById(alarmId: Long){
    boxAlarmInstance.query().equal(AlarmInstance_.alarmId, alarmId).build().remove()
}

fun deleteAlarmAndInstances(id: Long) {
    boxAlarmInstance.query().equal(AlarmInstance_.alarmId, id).build().remove()
    boxAlarm.remove(id)
}