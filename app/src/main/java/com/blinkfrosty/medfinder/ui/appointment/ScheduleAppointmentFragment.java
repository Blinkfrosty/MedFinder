package com.blinkfrosty.medfinder.ui.appointment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.blinkfrosty.medfinder.dataaccess.datastructure.OfficeHours;
import com.blinkfrosty.medfinder.dataaccess.datastructure.DaySchedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleAppointmentFragment extends Fragment {

    private CalendarView calendarView;
    private Spinner timeSpinner;
    private TextView errorTextView;
    private EditText reasonEditText;
    private Button scheduleButton;
    private Doctor doctor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_appointment, container, false);

        calendarView = view.findViewById(R.id.calendar_view);
        timeSpinner = view.findViewById(R.id.time_spinner);
        errorTextView = view.findViewById(R.id.error_text_view);
        reasonEditText = view.findViewById(R.id.reason_edit_text);
        scheduleButton = view.findViewById(R.id.confirm_appointment_button);

        if (getArguments() != null) {
            doctor = (Doctor) getArguments().getSerializable("doctor");
            setupCalendar();
        }

        reasonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateFields();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validateFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                validateFields();
            }
        });

        scheduleButton.setOnClickListener(v -> Log.d("ScheduleAppointment", "Confirm appointment button clicked"));

        return view;
    }

    private void setupCalendar() {
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            updateAvailableTimes(selectedDate);
        });

        // Set the current date on the CalendarView
        Calendar calendar = Calendar.getInstance();
        long currentDate = calendar.getTimeInMillis();
        calendarView.setDate(currentDate, false, true);
        updateAvailableTimes(calendar);
    }

    private void updateAvailableTimes(Calendar selectedDate) {
        int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
        DaySchedule daySchedule = getDaySchedule(dayOfWeek);

        if (daySchedule != null && daySchedule.isAvailable()) {
            List<String> timeSlots = generateTimeSlots(daySchedule.getStartTime(), daySchedule.getEndTime());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, timeSlots);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timeSpinner.setAdapter(adapter);
            timeSpinner.setEnabled(true);
            errorTextView.setVisibility(View.GONE);
        } else {
            timeSpinner.setAdapter(null);
            timeSpinner.setEnabled(false);
            errorTextView.setText(R.string.error_day_unavailable);
            errorTextView.setVisibility(View.VISIBLE);
            validateFields();
        }
    }

    private DaySchedule getDaySchedule(int dayOfWeek) {
        OfficeHours officeHours = doctor.getOfficeHours();
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return officeHours.getMonday();
            case Calendar.TUESDAY:
                return officeHours.getTuesday();
            case Calendar.WEDNESDAY:
                return officeHours.getWednesday();
            case Calendar.THURSDAY:
                return officeHours.getThursday();
            case Calendar.FRIDAY:
                return officeHours.getFriday();
            case Calendar.SATURDAY:
                return officeHours.getSaturday();
            case Calendar.SUNDAY:
                return officeHours.getSunday();
            default:
                return null;
        }
    }

    private List<String> generateTimeSlots(String startTime, String endTime) {
        List<String> timeSlots = new ArrayList<>();
        SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a");

        try {
            Date start = sdf24.parse(startTime);
            Date end = sdf24.parse(endTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            while (calendar.getTime().before(end)) {
                timeSlots.add(sdf12.format(calendar.getTime()));
                calendar.add(Calendar.MINUTE, 30);
            }
        } catch (Exception e) {
            Log.e("ScheduleAppointment", "Error generating time slots - " + e.getMessage());
        }

        return timeSlots;
    }

    private void validateFields() {
        String reasonText = reasonEditText.getText().toString().trim();
        boolean isReasonValid = !reasonText.isEmpty();
        boolean isTimeSelected = timeSpinner.getSelectedItem() != null;

        scheduleButton.setEnabled(isReasonValid && isTimeSelected);
    }
}