package com.example.planetze.tracker;

import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.example.planetze.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class SelectedDateDecorator implements DayViewDecorator {
    private final CalendarDay selectedDate;
    private Context context;

    public SelectedDateDecorator(CalendarDay selectedDate, Context context) {
        this.selectedDate = selectedDate;
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(selectedDate); // Apply only to the selected date
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(ContextCompat.getDrawable(context, R.drawable.teal_circle)); // Custom drawable
        view.addSpan(new ForegroundColorSpan(Color.WHITE)); // Set text color to white
    }

}
