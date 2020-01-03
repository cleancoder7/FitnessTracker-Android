package com.flinesoft.fitnesstracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.hours

@ExperimentalTime
@Entity(tableName = "Workouts")
data class Workout(var type: Type, var startDate: DateTime, var endDate: DateTime) {
    enum class Type {
        CARDIO, MUSCLE_BUILDING
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    val recoveryDuration: Duration
        get() = when (type) {
            Type.CARDIO -> 24.hours
            Type.MUSCLE_BUILDING -> 48.hours
        }
}
