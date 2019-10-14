package com.cloudsourceit.alarmmanagerdemo.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.IntDef
import androidx.annotation.LongDef
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem
import java.util.*
import kotlin.collections.ArrayList

const val FINISH_NONE = 0
const val FINISH_ONE = 1
const val FINISH_AFFINITY = 2

@IntDef(FINISH_NONE, FINISH_ONE, FINISH_AFFINITY)
@Retention(AnnotationRetention.SOURCE)
annotation class FinishMode

const val STATUS_STANDBY = 0L
const val STATUS_FIRED = 1L
const val STATUS_SNOOZED = 2L

@LongDef(STATUS_STANDBY, STATUS_FIRED, STATUS_SNOOZED)
@Retention(AnnotationRetention.SOURCE)
annotation class AlarmStatus

inline fun <reified T : Activity> Activity.startActivity(
    requestCode: Int = -1, extras: Bundle? = null,
    @FinishMode finishMode: Int = FINISH_NONE
) {
    val intent = Intent(this, T::class.java)
    extras?.apply {
        intent.putExtras(extras)
    }
    when (finishMode) {
        FINISH_ONE -> finish()
        FINISH_AFFINITY -> finishAffinity()
    }
    if (requestCode != -1) {
        startActivityForResult(intent, requestCode)
    } else {
        startActivity(intent)
    }
}

fun EditText.text(): String{
    return this.text.toString()
}

fun ArrayList<WeekdaysDataItem>.getArrayOfDays(): Array<Int?>{
    val array = arrayOfNulls<Int>(this.size)
    for((index, day) in this.withIndex()){
        array[index] = day.calendarDayId
    }
    return array
}

fun Long.toCalendar(): Calendar{
    return Calendar.getInstance().apply {
        timeInMillis = this@toCalendar
    }
}

fun Long.toClockHour(): String{
    val calendar = toCalendar()
    return calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE)
}