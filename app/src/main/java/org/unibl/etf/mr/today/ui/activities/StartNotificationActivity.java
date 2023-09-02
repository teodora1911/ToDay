package org.unibl.etf.mr.today.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.unibl.etf.mr.today.R;
import org.unibl.etf.mr.today.adapter.NotificationItemsAdapter;
import org.unibl.etf.mr.today.db.ApplicationDb;
import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.ui.home.HomeFragment;

import java.lang.ref.WeakReference;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class StartNotificationActivity extends AppCompatActivity {

    private List<Item> items;
    private NotificationItemsAdapter adapter;

    private RecyclerView recyclerView;

    private ApplicationDb database;
    TextView emptyListTextView;

    private Date start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_notification);

        start = (Date) getIntent().getSerializableExtra("start");
        end = (Date) getIntent().getSerializableExtra("end");

        emptyListTextView = findViewById(R.id.notifEmptyListTextView);

        recyclerView = findViewById(R.id.notifRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
        adapter = new NotificationItemsAdapter(items, this);
        recyclerView.setAdapter(adapter);

        showActivities();
    }

    private void showActivities() {
        recyclerView.setAdapter(adapter);
        database = ApplicationDb.getInstance(this);
        new StartNotificationActivity.GetItemsBetweenDatesAsync(database, this::drawItems, start, end).execute();
    }

    public void drawItems(List<Item> newItems) {

        if(newItems == null || newItems.size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
            items.clear();
            items.addAll(newItems);
        }

        adapter.notifyDataSetChanged();
    }

    public static class GetItemsBetweenDatesAsync extends AsyncTask<Void, Void, List<Item>> {

        ApplicationDb database;
        Consumer<List<Item>> callback;

        Date start, end;

        public GetItemsBetweenDatesAsync(ApplicationDb database, Consumer<List<Item>> callback, Date start, Date end) {
            this.database = database;
            this.callback = callback;
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<Item> doInBackground(Void... voids) {
            return database.itemDao().getItems(start, end);
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            callback.accept(items);
        }
    }
}