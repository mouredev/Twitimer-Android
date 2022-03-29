package com.mouredev.twitimer.usecases.common.rows

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.TimerItemBinding
import com.mouredev.twitimer.model.session.SortedStreaming
import com.mouredev.twitimer.usecases.user.UserRouter
import com.mouredev.twitimer.util.*
import com.mouredev.twitimer.util.extension.font
import java.util.*

/**
 * Created by MoureDev by Brais Moure on 15/6/21.
 * www.mouredev.com
 */
class CountdownRecyclerViewAdapter(val context: Context, var streamings: List<SortedStreaming>) : RecyclerView.Adapter<CountdownRecyclerViewAdapter.ViewHolder>() {

    // Initialization

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = TimerItemBinding.bind(itemView)
        private var timer: CountDownTimer? = null

        @SuppressLint("SetTextI18n")
        fun bind(streaming: SortedStreaming) = with(itemView) {

            streaming.streamer.let { user ->

                // Streamer
                binding.textViewUser.text = user.displayName ?: ""
                binding.textViewSchedule.text = streaming.schedule.formattedDate()
                binding.textViewTimer.text = ""
                binding.textViewHours.text = "${streaming.schedule.duration}h"

                binding.textViewInfo.text = streaming.schedule.title
                if (binding.textViewInfo.text.isEmpty()) {
                    binding.linearLayoutInfo.visibility = View.GONE
                } else {
                    binding.linearLayoutInfo.visibility = View.VISIBLE
                }

                // Avatar
                UIUtil.loadAvatar(context, user.profileImageUrl, user.login, binding.imageViewAvatar)

                // Channel
                binding.buttonChannel.setOnClickListener {
                    user.login?.let { login ->
                        val url = "${Constants.TWITCH_PROFILE_URI}${login}"
                        Util.openBrowser(context, url)
                    }
                }

                // OnClick
                itemView.setOnClickListener {
                    UserRouter().launch(context, user)
                }

                // Timer
                timerFunction(context, streaming, binding.textViewTimer, binding.imageViewClockIcon)
            }
        }

        private fun timerFunction(context: Context, streaming: SortedStreaming, textViewTimer: TextView, imageViewClockIcon: ImageView) {

            timer?.cancel()

            imageViewClockIcon.setImageResource(R.drawable.time_clock_circle)
            imageViewClockIcon.setColorFilter(ContextCompat.getColor(context, R.color.light))

            val endDate = streaming.schedule.date.time - Date().time

            timer = object : CountDownTimer(endDate, 1000) {

                override fun onTick(millisUntilFinished: Long) {

                    textViewTimer.text = Util.countdown(context, millisUntilFinished)
                }

                override fun onFinish() {

                    textViewTimer.text = context.getText(R.string.countdown_live).toString().uppercase()
                    imageViewClockIcon.setImageResource(R.drawable.button_record)
                    imageViewClockIcon.setColorFilter(ContextCompat.getColor(context, R.color.live))
                    timer?.cancel()
                }

            }

            timer?.start()
        }

    }

    override fun getItemCount(): Int {
        return streamings.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.timer_item, parent, false)
        val viewHolder = ViewHolder(view)

        // UI

        val binding = viewHolder.binding

        binding.textViewUser.font(FontSize.HEAD, color = ContextCompat.getColor(context, R.color.light))
        binding.textViewSchedule.font(FontSize.BUTTON, FontType.LIGHT, ContextCompat.getColor(context, R.color.light))
        binding.textViewInfo.font(FontSize.CAPTION, color = ContextCompat.getColor(context, R.color.light))
        binding.textViewTimer.font(FontSize.SUBHEAD, FontType.BOLD, ContextCompat.getColor(context, R.color.light))
        binding.textViewHours.font(FontSize.SUBHEAD, FontType.BOLD, ContextCompat.getColor(context, R.color.light))

        binding.textViewTimer.text = ""
        binding.textViewHours.text = ""

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(streamings[position])
    }

}