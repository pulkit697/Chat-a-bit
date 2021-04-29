package com.example.chatapp.util

import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import com.example.chatapp.R
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
import java.util.Calendar.*

val Date.calendar:Calendar
get() {
    val cal = getInstance()
    cal.time = this
    return cal
}

private fun getCurrentLocale(context: Context):Locale{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        context.resources.configuration.locales[0]
    else
        context.resources.configuration.locale
}

fun Date.isThisWeek():Boolean{
    val presentCalendar = Calendar.getInstance()
    val presentWeek = presentCalendar.get(Calendar.WEEK_OF_YEAR)
    val presentYear = presentCalendar.get(Calendar.YEAR)

    val thisCalendar = Calendar.getInstance()
    thisCalendar.time = this
    val thisWeek = thisCalendar.get(Calendar.WEEK_OF_YEAR)
    val thisYear = thisCalendar.get(Calendar.YEAR)

    return thisWeek == presentWeek && thisYear == presentYear
}

fun Date.isToday():Boolean = DateUtils.isToday(this.time)

fun Date.isYesterday():Boolean
{
    val yesterday = getInstance()
    yesterday.add(DAY_OF_YEAR,-1)
    return yesterday.get(YEAR) == calendar.get(YEAR)
            && yesterday.get(DAY_OF_YEAR) == calendar.get(DAY_OF_YEAR)
}

fun Date.isThisYear() = getInstance().get(YEAR) == calendar.get(YEAR)

fun Date.isSameDayAs(date:Date) =
    this.calendar.get(DAY_OF_YEAR) == date.calendar.get(DAY_OF_YEAR)

fun Date.formatAsTime():String {
    val hour = calendar.get(HOUR_OF_DAY).toString().padStart(2,'0')
    val minutes = calendar.get(MINUTE).toString().padStart(2,'0')
    return "$hour:$minutes"
}

fun Date.formatAsYesterday(context:Context):String = context.getString(R.string.yesterday)

fun Date.formatAsWeekDay(context:Context):String
{
    return when(calendar.get(DAY_OF_WEEK))
    {
        MONDAY -> "monday"
        TUESDAY -> "tuesday"
        WEDNESDAY -> "wednesday"
        THURSDAY -> "thursday"
        FRIDAY -> "friday"
        SATURDAY -> "saturday"
        else ->
            SimpleDateFormat("d LLL", getCurrentLocale(context)).format(this)
    }
}

fun Date.formatAsFull(context: Context, abbreviated: Boolean = false): String {
    val month = if (abbreviated) "LLL" else "LLLL"

    return SimpleDateFormat("d $month YYYY", getCurrentLocale(context))
        .format(this)
}

fun Date.formatAsListItem(context: Context): String {
    val currentLocale = getCurrentLocale(context)

    return when {
        isToday() -> formatAsTime()
        isYesterday() -> formatAsYesterday(context)
        isThisWeek() -> formatAsWeekDay(context)
        isThisYear() -> {
            SimpleDateFormat("d LLL", currentLocale).format(this)
        }
        else -> {
            formatAsFull(context, abbreviated = true)
        }
    }
}

fun Date.formatAsHeader(context: Context): String {
    return when {
        isToday() -> "today"
        isYesterday() -> formatAsYesterday(context)
        isThisWeek() -> formatAsWeekDay(context)
        isThisYear() -> {
            SimpleDateFormat("d LLLL", getCurrentLocale(context))
                .format(this)
        }
        else -> {
            formatAsFull(context, abbreviated = false)
        }
    }
}

