package com.flinesoft.fitnesstracker.ui.workouts

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.flinesoft.fitnesstracker.R
import com.flinesoft.fitnesstracker.globals.BETWEEN_WORKOUTS_POSITIVE_DAYS
import com.flinesoft.fitnesstracker.globals.BETWEEN_WORKOUTS_POSITIVE_PLUS_WARNING_DAYS
import com.flinesoft.fitnesstracker.globals.extensions.DateFormatExt
import com.flinesoft.fitnesstracker.model.Workout
import java.text.DateFormatSymbols
import java.util.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.days
import kotlin.time.hours

@ExperimentalTime
class WorkoutsHistoryCellViewModel(application: Application, val workout: Workout, val betweenWorkoutsDuration: Duration) : AndroidViewModel(application) {
    fun betweenWorkoutsIconDrawable(): Drawable? = when (betweenWorkoutsDuration) {
        in 0.hours..workout.recoveryDuration.div(2) ->
            ContextCompat.getDrawable(getApplication(), R.drawable.ic_workouts_time_between_negative)

        in workout.recoveryDuration.div(2)..workout.recoveryDuration ->
            ContextCompat.getDrawable(getApplication(), R.drawable.ic_workouts_time_between_warning)

        in workout.recoveryDuration..(workout.recoveryDuration + BETWEEN_WORKOUTS_POSITIVE_DAYS.days) ->
            ContextCompat.getDrawable(getApplication(), R.drawable.ic_workouts_time_between_positive)

        in (workout.recoveryDuration + BETWEEN_WORKOUTS_POSITIVE_DAYS.days)..(workout.recoveryDuration + BETWEEN_WORKOUTS_POSITIVE_PLUS_WARNING_DAYS.days) ->
            ContextCompat.getDrawable(getApplication(), R.drawable.ic_workouts_time_between_warning)

        else ->
            ContextCompat.getDrawable(getApplication(), R.drawable.ic_workouts_time_between_negative)
    }

    fun betweenWorkoutsText(context: Context): String = betweenWorkoutsDuration.toComponents { days, hours, _, _, _ ->
        val daysString = context.resources.getQuantityString(R.plurals.global_duration_days, days, days)
        val hoursString = context.resources.getQuantityString(R.plurals.global_duration_hours, hours, hours)
        context.getString(R.string.workouts_history_cell_between_workouts_duration, daysString, hoursString)
    }

    fun monthText(): String = DateFormatSymbols().shortMonths[workout.startDate.monthOfYear - 1].substring(0..2).toUpperCase(Locale.getDefault())
    fun dayText(): String = workout.startDate.dayOfMonth.toString().padStart(2, '0')

    fun workoutTypeIconDrawable(context: Context): Drawable? = when (workout.type) {
        Workout.Type.CARDIO ->
            ContextCompat.getDrawable(context, R.drawable.ic_workouts_cardio)

        Workout.Type.MUSCLE_BUILDING ->
            ContextCompat.getDrawable(context, R.drawable.ic_workouts_muscle_building)
    }

    fun workoutTypeText(context: Context): String = when (workout.type) {
        Workout.Type.CARDIO ->
            context.getString(R.string.models_workout_type_cardio)

        Workout.Type.MUSCLE_BUILDING ->
            context.getString(R.string.models_workout_type_muscle_building)
    }

    fun timeText(context: Context): String = context.getString(
        R.string.global_duration_time_interval,
        DateFormatExt.timeShort().format(workout.startDate.toDate()),
        DateFormatExt.timeShort().format(workout.endDate.toDate())
    )
}
