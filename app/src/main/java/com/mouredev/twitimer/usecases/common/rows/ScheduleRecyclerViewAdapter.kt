package com.mouredev.twitimer.usecases.common.rows

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.ScheduleItemBinding
import com.mouredev.twitimer.model.domain.UserSchedule
import com.mouredev.twitimer.model.domain.UserSearch
import com.mouredev.twitimer.model.domain.WeekdayType
import com.mouredev.twitimer.util.*
import com.mouredev.twitimer.util.extension.*
import java.util.*


/**
 * Created by MoureDev by Brais Moure on 5/31/21.
 * www.mouredev.com
 */
class ScheduleRecyclerViewAdapter(val context: Context, var schedules: MutableList<UserSchedule>, private val readOnly: Boolean, private val updated: (schedule: UserSchedule) -> Unit) :
    RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder>() {

    // Initialization

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ScheduleItemBinding.bind(itemView)

        private fun hoursList(readOnly: Boolean): List<String> {
            val list = arrayListOf<String>()
            for (number in 1..24) {
                list.add(if (readOnly) "${number}h" else "+${number}h")
            }
            return list
        }

        fun bind(schedule: UserSchedule, readOnly: Boolean, updated: (schedule: UserSchedule) -> Unit) = with(itemView) {

            val weekday = weekday(schedule, readOnly)

            if (weekday == WeekdayType.CUSTOM) {
                binding.imageViewClockIcon.visibility = View.GONE
                binding.buttonDate.picker(ContextCompat.getColor(context, if (readOnly) R.color.light else R.color.primary))
                binding.textViewWeekday.visibility = View.GONE
                binding.imageViewCustom.visibility = View.VISIBLE

            } else {
                binding.imageViewClockIcon.visibility = View.VISIBLE
                binding.buttonDate.picker(ContextCompat.getColor(context, R.color.light))
                binding.textViewWeekday.visibility = View.VISIBLE
                binding.imageViewCustom.visibility = View.GONE
                binding.textViewWeekday.text = context.getString(weekday.nameKey).first().toString().uppercaseFirst()
            }

            binding.editTextInfo.setText(schedule.title)
            binding.editTextInfo.visibility = View.VISIBLE

            if (!readOnly) {
                binding.editTextInfo.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        schedule.title = binding.editTextInfo.text.toString()
                        hideSoftInput()
                        binding.editTextInfo.clearFocus()
                        return@setOnEditorActionListener true
                    } else {
                        return@setOnEditorActionListener false
                    }
                }

                binding.editTextInfo.addTextChangedListener(object : TextWatcher {

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        schedule.title = binding.editTextInfo.text.toString()
                        updated(schedule)
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // Do nothing
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Do nothing
                    }
                })
            } else {
                if (schedule.title.isEmpty()) {
                    binding.editTextInfo.visibility = View.GONE
                }
            }

            binding.textViewDay.text = context.getString(weekday.nameKey)

            val spinnerAdapter = ArrayAdapter(context, R.layout.hour_spinner_dropdown, hoursList(readOnly))
            binding.spinnerHour.adapter = spinnerAdapter
            binding.spinnerHour.setSelection(schedule.duration - 1)

            if (!readOnly) {

                binding.spinnerHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                        val checkedTextView = (parent?.getChildAt(0) as CheckedTextView)
                        checkedTextView.font(FontSize.SUBHEAD, FontType.LIGHT, ContextCompat.getColor(context, R.color.light))

                        schedule.duration = position + 1
                        updated(schedule)
                    }
                }

                binding.imageViewCheck.setOnClickListener {
                    schedule.enable = !schedule.enable
                    setupEnable(schedule, readOnly)
                    updated(schedule)
                }

                binding.buttonDate.setOnClickListener {

                    val calendar = Calendar.getInstance()
                    calendar.time = schedule.date
                    val currentHour = calendar[Calendar.HOUR_OF_DAY]
                    val currentMinute = calendar[Calendar.MINUTE]

                    if (weekday == WeekdayType.CUSTOM) {
                        val datePickerDialog = DatePickerDialog(context, R.style.CustomDatePickerTheme, { _, year, month, dayOfMonth ->

                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                            TimePickerDialog(context, R.style.CustomTimePickerTheme, { _, hourOfDay, minute ->

                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                schedule.date = calendar.time

                                setupTime(schedule, readOnly)
                                updated(schedule)

                            }, currentHour, currentMinute, android.text.format.DateFormat.is24HourFormat(context)).show()

                        }, 0, 9, 0)
                        datePickerDialog.datePicker.minDate = Date().time
                        datePickerDialog.show()
                    } else {
                        TimePickerDialog(context, R.style.CustomTimePickerTheme, { _, hourOfDay, minute ->

                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            schedule.date = calendar.time

                            setupTime(schedule, readOnly)
                            updated(schedule)

                        }, currentHour, currentMinute, android.text.format.DateFormat.is24HourFormat(context)).show()
                    }
                }

                binding.imageViewCheck.visibility = View.VISIBLE

            }

            setupEnable(schedule, readOnly)
            setupTime(schedule, readOnly)
        }

        private fun setupEnable(schedule: UserSchedule, readOnly: Boolean) = with(itemView) {

            when {
                readOnly -> {
                    binding.imageViewCheck.visibility = View.GONE
                    binding.textViewDay.isEnabled = false
                    binding.buttonDate.isEnabled = false
                    binding.editTextInfo.isEnabled = false
                    binding.spinnerHour.isEnabled = false
                    binding.layoutSchedule.alpha = 1f

                } schedule.enable -> {
                    binding.imageViewCheck.setImageResource(R.drawable.check_circle)
                    binding.textViewDay.isEnabled = true
                    binding.buttonDate.isEnabled = true
                    binding.editTextInfo.isEnabled = true
                    binding.spinnerHour.isEnabled = true
                    binding.layoutSchedule.alpha = 1f

                } else -> {
                    binding.imageViewCheck.setImageResource(R.drawable.cursor_select_circle)
                    binding.textViewDay.isEnabled = false
                    binding.buttonDate.isEnabled = false
                    binding.editTextInfo.isEnabled = false
                    binding.spinnerHour.isEnabled = false
                    binding.layoutSchedule.alpha = UIConstants.VIEW_OPACITY
                }
            }
        }

        private fun setupTime(schedule: UserSchedule, readOnly: Boolean) = with(itemView) {

            if (weekday(schedule, readOnly) == WeekdayType.CUSTOM) {
                binding.buttonDate.text = schedule.date.mediumFormat()
            } else {
                binding.buttonDate.text = schedule.date.shortFormat()
            }
        }

        private fun weekday(schedule: UserSchedule, readOnly: Boolean): WeekdayType {
            return if (readOnly) schedule.currentWeekDay else schedule.weekDay
        }

    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false)
        val viewHolder = ViewHolder(view)

        // UI

        val binding = viewHolder.binding

        binding.textViewWeekday.font(FontSize.TITLE, FontType.LIGHT, ContextCompat.getColor(context, R.color.text))
        binding.textViewDay.font(FontSize.HEAD, color = ContextCompat.getColor(context, R.color.light))
        binding.editTextInfo.font(FontSize.BODY, color = ContextCompat.getColor(context, R.color.text))
        binding.editTextInfo.hint = context.getString(R.string.schedule_event_placeholder)
        binding.editTextInfo.imeOptions = EditorInfo.IME_ACTION_DONE

        if (readOnly) {
            binding.layoutScheduleContent.updatePadding(left = Util.dpToPixel(context, Size.NONE.size).toInt())
            binding.editTextInfo.setBackgroundColor(ContextCompat.getColor(context, R.color.secondary))
            binding.editTextInfo.setTextColor(ContextCompat.getColor(context, R.color.light))
            binding.editTextInfo.isEnabled = false
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(schedules[position], readOnly, updated)
    }

}