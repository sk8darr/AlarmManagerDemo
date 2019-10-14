package com.cloudsourceit.alarmmanagerdemo.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class AlarmInstance(@Id var instanceId: Long = 0, var weekDay: Int = 1, var time: Long = 0){
    lateinit var alarm: ToOne<Alarm>
}