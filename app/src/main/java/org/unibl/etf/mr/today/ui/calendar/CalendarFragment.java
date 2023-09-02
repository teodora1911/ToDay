package org.unibl.etf.mr.today.ui.calendar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import org.unibl.etf.mr.today.R;
import org.unibl.etf.mr.today.adapter.NotificationItemsAdapter;
import org.unibl.etf.mr.today.db.ApplicationDb;
import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.ui.activities.StartNotificationActivity;
import org.unibl.etf.mr.today.databinding.FragmentCalendarBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CalendarFragment extends Fragment {

    ApplicationDb database;
    List<Item> activities;
    CalendarView calendarView;
    RecyclerView recyclerView;
    TextView emptyListTextView;

    private FragmentCalendarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        database = ApplicationDb.getInstance(getActivity());

        calendarView = root.findViewById(R.id.calendarView);
        calendarView.setOnDayClickListener(eventDay -> CalendarFragment.this.getTodayActivities(eventDay.getCalendar().getTime()));

        recyclerView = root.findViewById(R.id.calendarRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        emptyListTextView = root.findViewById(R.id.calendarEmptyListTextView);

        new CalendarFragment.GetItemsAsync(database, this::markActivitiesOnCalendar).execute();
        getTodayActivities(new Date());

        return root;
    }

    private void getTodayActivities(Date date){
        Date start = new Date(date.getTime());
        start.setHours(0);
        start.setMinutes(0);

        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.add(Calendar.DAY_OF_YEAR, 1);
        Date end = c.getTime();

        // get items for selected date
        new StartNotificationActivity.GetItemsBetweenDatesAsync(database, this::displayTodayActivities, start, end).execute();
    }

    private void displayTodayActivities(List<Item> items){
        if(items == null || items.size() == 0){
            emptyListTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyListTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            NotificationItemsAdapter adapter = new NotificationItemsAdapter(items, getActivity());
            recyclerView.setAdapter(adapter);
        }
    }

    private void markActivitiesOnCalendar(List<Item> items) {
        List<EventDay> events = new ArrayList<>();

        for(Item activity : items){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(activity.getTimestamp());
            EventDay eventDay = new EventDay(calendar, R.drawable.ic_calendar_dot);
            events.add(eventDay);
        }

        calendarView.setEvents(events);
    }

    private static class GetItemsAsync extends AsyncTask<Void, Void, List<Item>> {

        ApplicationDb database;
        Consumer<List<Item>> callback;
        GetItemsAsync(ApplicationDb database, Consumer<List<Item>> callback) {
            this.database = database;
            this.callback = callback;
        }

        @Override
        protected List<Item> doInBackground(Void... voids) {
            return database.itemDao().getItems();
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            if (items != null && items.size() > 0) {
                System.out.println("Size: " + items.size());
                callback.accept(items);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}