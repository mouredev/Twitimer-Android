package com.mouredev.twitimer.usecases.common.rows

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.SearchItemBinding
import com.mouredev.twitimer.model.domain.BroadcasterType
import com.mouredev.twitimer.model.domain.User
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.usecases.user.UserRouter
import com.mouredev.twitimer.util.*
import com.mouredev.twitimer.util.extension.font
import com.mouredev.twitimer.util.extension.uppercaseFirst


/**
 * Created by MoureDev by Brais Moure on 6/14/21.
 * www.mouredev.com
 */
class SearchRecyclerViewAdapter(val context: Context, var users: List<User>, val listener: (User) -> Unit) :
    RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    // Initialization

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = SearchItemBinding.bind(itemView)
        private var added = false

        fun bind(user: User, listener: (User) -> Unit) = with(itemView) {

            binding.imageViewCalendar.setOnClickListener {
                add(context, user, listener)
            }

            // User
            binding.textViewUser.text = user.displayName ?: ""
            binding.imageViewHoliday.visibility = if (user.settings?.onHolidays == true) { View.VISIBLE } else { View.GONE }

            // Avatar
            UIUtil.loadAvatar(context, user.profileImageUrl, user.login, binding.imageViewAvatar)

            // Channel
            binding.buttonChannel.setOnClickListener {
                user.login?.let { login ->
                    val url = "${Constants.TWITCH_PROFILE_URI}${login}"
                    Util.openBrowser(context, url)
                }
            }

            setupAdded(user)

            // OnClick
            itemView.setOnClickListener {
                UserRouter().launch(context, user)
            }
        }

        private fun setupAdded(user: User) = with(itemView) {

            user.login?.let { login ->

                added = Session.instance.user?.followedUsers?.contains(login) ?: false
                if (added) {
                    binding.imageViewCalendar.setImageResource(R.drawable.calendar_remove)
                    binding.layoutSearch.alpha = 1f
                } else {
                    binding.imageViewCalendar.setImageResource(R.drawable.calendar_add)
                    binding.layoutSearch.alpha = UIConstants.VIEW_OPACITY
                }
            }
        }

        private fun add(context: Context, user: User, listener: (User) -> Unit) {

            if (!added && Session.instance.user?.followedUsers?.count() == Constants.MAX_STREAMERS) {
                // No se pueden añadir más streamers
                UIUtil.showAlert(context, context.getString(R.string.search_maxstreamers_alert_title), context.getString(R.string.search_maxstreamers_alert_body), context.getString(R.string.accept))
            } else if (added) {
                // Se notifica el borrado
                UIUtil.showAlert(
                    context,
                    context.getString(R.string.search_removestreamer_alert_title, user.displayName ?: ""),
                    context.getString(R.string.search_removestreamer_alert_body),
                    context.getString(R.string.accept),
                    {
                        save(context, user, listener)
                    },
                    context.getString(R.string.cancel)
                )

            } else {
                save(context, user, listener)
            }
        }

        private fun save(context: Context, user: User, listener: (User) -> Unit) {

            Session.instance.saveFollow(context, user)
            listener(user)
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
        binding.textViewUser.maxLines = 2
        binding.buttonChannel.background = ContextCompat.getDrawable(context, R.drawable.channel_button_round_dark)
        binding.textViewChannelTitle.font(FontSize.CAPTION, FontType.LIGHT, ContextCompat.getColor(context, R.color.light))

        // Localize
        binding.textViewChannelTitle.text = context.getText(R.string.user_seechannel)

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position], listener)
    }

}