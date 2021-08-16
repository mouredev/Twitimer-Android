package com.mouredev.twitimer.util.extension

import com.mouredev.twitimer.model.domain.WeekdayType
import com.mouredev.twitimer.util.Constants
import org.threeten.bp.*
import org.threeten.bp.temporal.TemporalAdjusters
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MoureDev by Brais Moure on 5/10/21.
 * www.mouredev.com
 */

fun Date.toJSON(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.getDefault())
    return formatter.format(this)
}

fun Date.longFormat(): String {
    return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault()).format(this).uppercaseFirst()
}

fun Date.mediumFormat(): String {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(this).uppercaseFirst()
}

fun Date.shortFormat(): String {
    return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(this).format(this).uppercaseFirst()
}

fun Date.next(weekday: Weekday, considerToday: Boolean = false, referenceDate: Date? = null, duration: Int? = null, save: Boolean = false): Date {

    val dayName = weekday.englishName
    val weekdaysName = getWeekDaysInEnglish().map { it.lowercase() }
    val searchWeekdayIndex = weekdaysName.indexOfFirst { it == dayName } + 1
    val calendar = GregorianCalendar()
    calendar.time = this

    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    if (considerToday && dayOfWeek == searchWeekdayIndex) {
        if (referenceDate != null && duration != null && Date(referenceDate.time + (1000 * 60 * 60 * duration)) > this) {
            return referenceDate
        } else if (Date() <= Date(this.time + (1000 * 60 * 60 * (duration ?: 0)))
            || Date(this.time + (1000 * 60 * 60 * (duration ?: 0))) <= Date()
            || save && this > Date()) {
            return this
        }
    }

    var localDate = LocalDate.now()
    localDate = localDate.with(TemporalAdjusters.next(weekday.dayOfWeek()))

    val instant: Instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
    val nextDate = DateTimeUtils.toDate(instant)
    val nextCalendar = Calendar.getInstance()
    nextCalendar.time = nextDate
    nextCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
    nextCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
    nextCalendar.set(Calendar.SECOND, 0)

    return nextCalendar.time
}


fun Date.getWeekDayType(originalWeekDay: Int?): WeekdayType {

    if (originalWeekDay != null && originalWeekDay != WeekdayType.CUSTOM.index) {
        return weekdayType()
    }
    return WeekdayType.CUSTOM
}

private fun getWeekDaysInEnglish(): List<String> {
    val dateFormat = DateFormatSymbols.getInstance(Constants.DEFAULT_LOCALE)
    val list = dateFormat.shortWeekdays.toMutableList()
    // 8 Valores: Cadena vacía (para que el día 1 sea Sun), Sun, Mon... Sat
    // HACK: Eliminamos el valor vacío
    list.removeFirst()
    return list
}

enum class Weekday(val englishName: String) {

    MONDAY("mon"),
    TUESDAY("tue"),
    WEDNESDAY("wed"),
    THURSDAY("thu"),
    FRIDAY("fri"),
    SATURDAY("sat"),
    SUNDAY("sun");

    fun dayOfWeek(): DayOfWeek {
        return when (this) {
            MONDAY -> DayOfWeek.MONDAY
            TUESDAY -> DayOfWeek.TUESDAY
            WEDNESDAY -> DayOfWeek.WEDNESDAY
            THURSDAY -> DayOfWeek.THURSDAY
            FRIDAY -> DayOfWeek.FRIDAY
            SATURDAY -> DayOfWeek.SATURDAY
            SUNDAY -> DayOfWeek.SUNDAY
        }
    }

}

fun Date.weekdayType(): WeekdayType {

    val calendar = GregorianCalendar(Constants.DEFAULT_LOCALE)
    calendar.time = this
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        1 -> WeekdayType.SUNDAY
        2 -> WeekdayType.MONDAY
        3 -> WeekdayType.TUESDAY
        4 -> WeekdayType.WEDNESDAY
        5 -> WeekdayType.THURSDAY
        6 -> WeekdayType.FRIDAY
        7 -> WeekdayType.SATURDAY
        else -> WeekdayType.CUSTOM
    }
}
