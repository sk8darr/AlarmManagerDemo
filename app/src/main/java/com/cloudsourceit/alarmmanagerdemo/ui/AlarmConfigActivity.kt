package com.cloudsourceit.alarmmanagerdemo.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.timePicker
import com.cloudsourceit.alarmmanagerdemo.KEY_ALARM_ID
import com.cloudsourceit.alarmmanagerdemo.R
import com.cloudsourceit.alarmmanagerdemo.model.Alarm
import com.cloudsourceit.alarmmanagerdemo.model.AlarmInstance
import com.cloudsourceit.alarmmanagerdemo.utils.*
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource
import kotlinx.android.synthetic.main.activity_alarm_config.*
import java.util.*
import kotlin.collections.ArrayList

class AlarmConfigActivity : AppCompatActivity(), WeekdaysDataSource.Callback {

    private val now = Calendar.getInstance()
    private lateinit var time: GregorianCalendar
    private var days: ArrayList<WeekdaysDataItem>? = ArrayList()
    private lateinit var viewStub: View
    private var alarm: Alarm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_config)

        WeekdaysDataSource(this, R.id.vs_day_picker).start(this)

        viewStub = findViewById<View>(R.id.vs_day_picker)
        viewStub.visibility = View.GONE

        time = now as GregorianCalendar
        tv_clock.text = time.timeInMillis.toClockHour()

        cb_repeat.setOnCheckedChangeListener { _, isChecked -> viewStub.visibility = if(isChecked) View.VISIBLE else View.GONE }

        val id: Long? = intent.extras?.getLong(KEY_ALARM_ID)

        id?.run {
            alarm = getAlarmById(this)?.apply {
                et_remainder.setText(this.content)
                tv_clock.text = time.toClockHour()
                cb_repeat.isChecked = this.repeatable
//                weekDays.setSelectedDays()
            }
        }

        bt_save.setOnClickListener{
            if(null == id) {
                addAlarms()
            } else {
                updateAlarms()
            }
            startNextAlarm(this)
            finish()
        }

        tv_clock.setOnClickListener {
            MaterialDialog(this).show {
                timePicker { _, datetime ->
                    datetime.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR))
                    datetime.set(Calendar.YEAR, now.get(Calendar.YEAR))
                    datetime.set(Calendar.MONTH, now.get(Calendar.MONTH))
                    time = GregorianCalendar()
                    time.timeInMillis = datetime.timeInMillis
                    (it as AppCompatTextView).text = datetime.timeInMillis.toClockHour()
                }
            }
        }
    }

    private fun addAlarms(){
        val alarmInstanceList = ArrayList<AlarmInstance>()
        time.run {
            val alarm = Alarm(title = "Title", content = "${et_remainder.text}", time = this.timeInMillis, repeatable = cb_repeat.isChecked)
            if(cb_repeat.isChecked && !days.isNullOrEmpty()){
                alarmInstanceList.addAll(createInstances(this, alarm))
            } else {
                val day = this.get(Calendar.DAY_OF_WEEK)
                val instance = AlarmInstance(weekDay = day, time = calculateNextDate(this, day).timeInMillis)
                instance.alarm.target = alarm
                alarmInstanceList.add(instance)
            }
            saveAlarmAndInstances(alarm, alarmInstanceList)
        }
    }

    private fun updateAlarms(){
        alarm?.also {
            it.title = "Title"
            it.content = et_remainder.text()
            it.time = time.timeInMillis
            it.repeatable = cb_repeat.isChecked
            deleteAllInstancesById(it.id)
            val newAlarmInstances = createInstances(time, it)
            saveAlarmAndInstances(it, newAlarmInstances)
        }
    }

    private fun createInstances(time: GregorianCalendar, alarm: Alarm): ArrayList<AlarmInstance>{
        val list = ArrayList<AlarmInstance>()
        days?.forEach {
            if(it.isSelected){
                val instance = AlarmInstance(weekDay = it.position + 1, time = calculateNextDate(time, it.position + 1).timeInMillis)
                instance.alarm.target = alarm
                list.add(instance)
            }
        }
        return list
    }

    override fun onWeekdaysSelected(p0: Int, p1: ArrayList<WeekdaysDataItem>?) {
        days = p1
    }

    override fun onWeekdaysItemClicked(p0: Int, p1: WeekdaysDataItem?) {
        //DO NOTHING
    }
}
