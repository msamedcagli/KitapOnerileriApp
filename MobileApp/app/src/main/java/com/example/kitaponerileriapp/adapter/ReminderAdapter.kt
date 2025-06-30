package com.example.kitaponerileriapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kitaponerileriapp.databinding.ItemReminderBinding
import com.example.kitaponerileriapp.model.Reminder

class ReminderAdapter(
    private val onEditClick: (Reminder) -> Unit,
    private val onDeleteClick: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private var reminders: MutableList<Reminder> = mutableListOf()

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder) {
            binding.textViewReminderDateTime.text = "${reminder.date} ${reminder.time}"
            binding.textViewReminderDescription.text = reminder.description
            binding.buttonEditReminder.setOnClickListener { onEditClick(reminder) }
            binding.buttonDeleteReminder.setOnClickListener { onDeleteClick(reminder) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        Log.d("ReminderAdapter", "Binding item at position: $position")
        holder.bind(reminders[position])
    }

    override fun getItemCount(): Int {
        val count = reminders.size
        Log.d("ReminderAdapter", "getItemCount: $count")
        return count
    }

    fun updateReminders(newReminders: List<Reminder>) {
        reminders.clear()
        reminders.addAll(newReminders)
        notifyDataSetChanged()
        Log.d("ReminderAdapter", "updateReminders called. New size: ${reminders.size}")
    }
}
