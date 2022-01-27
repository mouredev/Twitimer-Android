package com.mouredev.twitimer.usecases.common.rows

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.SearchItemBinding
import com.mouredev.twitimer.model.domain.UserSearch
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.util.*
import com.mouredev.twitimer.util.extension.font


/**
 * Created by MoureDev by Brais Moure on 6/14/21.
 * www.mouredev.com
 */
class SearchQueryRecyclerViewAdapter(val context: Context, var users: List<UserSearch>, val listener: (UserSearch) -> Unit) :
    RecyclerView.Adapter<SearchQueryRecyclerViewAdapter.ViewHolder>() {

    // Initialization

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = SearchItemBinding.bind(itemView)

        fun bind(user: UserSearch, listener: (UserSearch) -> Unit) = with(itemView) {

            // User
            binding.textViewUser.text = user.displayName ?: ""

            // Avatar
            UIUtil.loadAvatar(context, user.thumbnailUrl, user.broadcasterLogin, binding.imageViewAvatar)

            // Channel
            binding.buttonChannel.setOnClickListener {
                user.broadcasterLogin?.let { login ->
                    val url = "${Constants.TWITCH_PROFILE_URI}${login}"
                    Util.openBrowser(context, url)
                }
            }

            checkFollow(context, user)

            // OnClick
            itemView.setOnClickListener {
                listener(user)
            }
        }

        // Private

        private fun checkFollow(context: Context, user: UserSearch) {

            if (Session.instance.user?.followedUsers?.contains(user.broadcasterLogin) == true) {
                binding.layoutContent.setPadding(Util.dpToPixel(context, 32).toInt(), 0, 0,0)
                binding.imageViewCalendar.visibility = View.VISIBLE
            } else {
                binding.layoutContent.setPadding(0, 0, 0,0)
                binding.imageViewCalendar.visibility = View.GONE
            }
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        val viewHolder = ViewHolder(view)

        // UI

        val binding = viewHolder.binding

        binding.textViewUser.font(FontSize.BUTTON, FontType.BOLD, ContextCompat.getColor(context, R.color.light))
        binding.buttonChannel.background = ContextCompat.getDrawable(context, R.drawable.channel_button_round_dark)
        binding.textViewUser.maxLines = 1
        binding.imageViewCalendar.setImageResource(R.drawable.calendar)
        val padding10 = Util.dpToPixel(context, Size.SMALL_MEDIUM.size).toInt()
        val padding12 = Util.dpToPixel(context, Size.SMALL_MEDIUM.size + Size.EXTRA_SMALL.size).toInt()
        binding.imageViewCalendar.setPadding(padding10, padding10, padding12, padding12)
        binding.imageViewCalendar.isEnabled = false
        binding.imageViewHoliday.visibility = View.GONE

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position], listener)
    }

}