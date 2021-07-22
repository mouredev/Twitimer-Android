package com.mouredev.twitimer.usecases.common.rows

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.SearchQueryItemBinding
import com.mouredev.twitimer.model.domain.UserSearch
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

        val binding = SearchQueryItemBinding.bind(itemView)

        fun bind(user: UserSearch, listener: (UserSearch) -> Unit) = with(itemView) {

            // User
            binding.textViewUser.text = user.displayName ?: ""

            // Avatar
            UIUtil.loadAvatar(context, user.thumbnailUrl, user.broadcasterLogin, binding.imageViewAvatar)

            // OnClick
            itemView.setOnClickListener {
                listener(user)
            }
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.search_query_item, parent, false)
        val viewHolder = ViewHolder(view)

        // UI
        viewHolder.binding.textViewUser.font(FontSize.BUTTON, FontType.BOLD, ContextCompat.getColor(context, R.color.light))
        viewHolder.binding.textViewUser.maxLines = 1

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position], listener)
    }

}