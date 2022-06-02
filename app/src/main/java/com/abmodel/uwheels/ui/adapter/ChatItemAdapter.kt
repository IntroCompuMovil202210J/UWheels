package com.abmodel.uwheels.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abmodel.uwheels.R
import com.abmodel.uwheels.data.model.Chat
import com.abmodel.uwheels.databinding.ChatListItemBinding
import com.abmodel.uwheels.util.formatDateFromMillis
import com.abmodel.uwheels.util.formatTime

class ChatItemAdapter(
	private val onItemClicked: (Chat) -> Unit
) : ListAdapter<Chat, ChatItemAdapter.ChatItemViewHolder>(DiffCallback) {

	class ChatItemViewHolder(
		private var binding: ChatListItemBinding
	) : RecyclerView.ViewHolder(binding.root) {

		fun bind(chat: Chat) {
			binding.apply {
				name.text = chat.name
				source.text = chat.source.mainText
				destination.text = chat.destination.mainText
				chat.date.apply {
					date.text =
						date.context.getString(
							R.string.chat_date,
							this.millis?.let { formatDateFromMillis(it) } ?: "",
							this.hour?.let { hour ->
								this.minute?.let { minute ->
									formatTime(hour, minute)
								} ?: ""
							} ?: ""
						)
				}
			}
		}
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): ChatItemViewHolder {
		return ChatItemViewHolder(
			ChatListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(
		holder: ChatItemViewHolder, position: Int
	) {
		val current = getItem(position)
		holder.itemView.setOnClickListener {
			onItemClicked(current)
		}
		holder.bind(current)
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<Chat>() {
			override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
				return oldItem.rideId == newItem.rideId
			}

			override fun areContentsTheSame(
				oldItem: Chat,
				newItem: Chat
			): Boolean {
				return oldItem == newItem
			}
		}
	}
}