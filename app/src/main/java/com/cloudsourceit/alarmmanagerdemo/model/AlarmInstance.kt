package com.cloudsourceit.alarmmanagerdemo.model

import com.cloudsourceit.alarmmanagerdemo.utils.AlarmStatus
import com.cloudsourceit.alarmmanagerdemo.utils.STATUS_STANDBY
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class AlarmInstance(@Id var instanceId: Long = 0, var weekDay: Int = 1, var time: Long = 0, @AlarmStatus var status: Long = STATUS_STANDBY){
    lateinit var alarm: ToOne<Alarm>
}