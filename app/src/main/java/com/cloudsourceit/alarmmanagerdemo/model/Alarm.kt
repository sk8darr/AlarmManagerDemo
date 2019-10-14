package com.cloudsourceit.alarmmanagerdemo.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class Alarm(
    @Id(assignable = true) var id: Long = 0,
    var title: String = "Alarm 0",
    var content: String = "Test",
    var time: Long = 0,
    var repeatable: Boolean = false,                                                //  S M T W T F S
    var weekDays: Int = 0x00,                   //Save repeatable days in binary number 0 0 0 0 0 0 0
    var enabled: Boolean = true)