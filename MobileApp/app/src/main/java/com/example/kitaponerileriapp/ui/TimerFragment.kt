package com.example.kitaponerileriapp.ui

import com.example.kitaponerileriapp.R
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import android.provider.Settings
import android.net.Uri
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kitaponerileriapp.adapter.ReminderAdapter
import com.example.kitaponerileriapp.model.Reminder
import com.example.kitaponerileriapp.databinding.FragmentTimerBinding
import com.example.kitaponerileriapp.notification.NotificationReceiver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import androidx.navigation.navOptions

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()
    private lateinit var reminderAdapter: ReminderAdapter
    private val reminders = mutableListOf<Reminder>()
    private var editingReminder: Reminder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reminderAdapter = ReminderAdapter(
            onEditClick = { reminder ->
                editingReminder = reminder
                binding.editTextDate.setText(reminder.date)
                binding.editTextTime.setText(reminder.time)
                binding.editTextDescription.setText(reminder.description)
                binding.buttonSetReminder.text = "Hatırlatıcıyı Güncelle"
            },
            onDeleteClick = { reminder ->
                deleteReminder(reminder)
            }
        )
        setupRecyclerView()
        loadReminders()

        binding.editTextDate.setOnClickListener {
            showDatePicker()
        }

        binding.editTextTime.setOnClickListener {
            showTimePicker()
        }

        binding.buttonSetReminder.setOnClickListener {
            setReminder()
        }

        val navOptions = navOptions {
            anim {
                enter = R.anim.slide_in_left
                exit = R.anim.slide_out_right
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()  // Normal geri git
            requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupRecyclerView() {
        Log.d("TimerFragment", "Setting up RecyclerView with ${reminders.size} reminders.")
        binding.recyclerViewReminders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderAdapter
        }
    }

    private fun loadReminders() {
        val sharedPreferences = requireActivity().getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("reminders_list", null)
        val type = object : TypeToken<MutableList<Reminder>>() {}.type
        json?.let {
            reminders.addAll(gson.fromJson(it, type))
            Log.d("TimerFragment", "Loaded reminders: ${reminders.size}")
        } ?: run {
            Log.d("TimerFragment", "No reminders found in SharedPreferences.")
        }
        reminderAdapter.updateReminders(reminders)
    }

    private fun saveReminders() {
        val sharedPreferences = requireActivity().getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(reminders)
        editor.putString("reminders_list", json)
        editor.apply()
        Log.d("TimerFragment", "Saved ${reminders.size} reminders.")
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                updateTimeInView()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = java.text.SimpleDateFormat(myFormat, java.util.Locale.getDefault())
        binding.editTextDate.setText(sdf.format(calendar.time))
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm"
        val sdf = java.text.SimpleDateFormat(myFormat, java.util.Locale.getDefault())
        binding.editTextTime.setText(sdf.format(calendar.time))
    }

    private fun setReminder() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
                Toast.makeText(requireContext(), "Tam zamanlı alarm izni gerekli.", Toast.LENGTH_LONG).show()
                return
            }
        }

        val description = binding.editTextDescription.text.toString()
        val date = binding.editTextDate.text.toString()
        val time = binding.editTextTime.text.toString()

        if (description.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            return
        }

        val newReminder: Reminder
        val pendingIntentId: Int

        if (editingReminder != null) {
            // Update existing reminder
            newReminder = editingReminder!!.copy(
                date = date,
                time = time,
                description = description
            )
            val index = reminders.indexOfFirst { it.id == newReminder.id }
            if (index != -1) {
                reminders[index] = newReminder
            }
            pendingIntentId = newReminder.pendingIntentId
            Toast.makeText(requireContext(), "Hatırlatıcı güncellendi.", Toast.LENGTH_SHORT).show()
        } else {
            // Add new reminder
            pendingIntentId = System.currentTimeMillis().toInt() // Unique ID for PendingIntent
            newReminder = Reminder(
                id = System.currentTimeMillis(),
                date = date,
                time = time,
                description = description,
                pendingIntentId = pendingIntentId
            )
            reminders.add(newReminder)
            Toast.makeText(requireContext(), "Hatırlatıcı kuruldu.", Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("description", description)
            putExtra("reminderId", newReminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            pendingIntentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        saveReminders()
        reminderAdapter.updateReminders(reminders)
        clearInputFields()
    }

    private fun deleteReminder(reminder: Reminder) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            reminder.pendingIntentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        reminders.remove(reminder)
        saveReminders()
        reminderAdapter.updateReminders(reminders)
        Toast.makeText(requireContext(), "Hatırlatıcı silindi.", Toast.LENGTH_SHORT).show()
    }

    private fun clearInputFields() {
        binding.editTextDate.setText("")
        binding.editTextTime.setText("")
        binding.editTextDescription.setText("")
        binding.buttonSetReminder.text = "Hatırlatıcı Kur"
        editingReminder = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
