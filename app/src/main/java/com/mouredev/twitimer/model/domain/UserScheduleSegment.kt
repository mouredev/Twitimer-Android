package com.mouredev.twitimer.model.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by MoureDev by Brais Moure on 2/7/21.
 * www.mouredev.com
 */
data class UserSchedules(val data: UserSchedulesSegments? = null)

data class UserSchedulesSegments(val segments: List<UserScheduleSegment>? = null)

data class UserScheduleSegment(
    val id: String? = null,
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("end_time") val endTime: String? = null,
    val title: String? = null,
    @SerializedName("is_recurring") val isRecurring: Boolean? = null
)