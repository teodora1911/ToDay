package org.unibl.etf.mr.today.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import org.unibl.etf.mr.today.R;
import org.unibl.etf.mr.today.adapter.ImageAdapter;
import org.unibl.etf.mr.today.db.ApplicationDb;
import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.db.entities.Picture;
import org.unibl.etf.mr.today.utils.DateTimeProvider;
import org.unibl.etf.mr.today.utils.FileSystemManager;
import org.unibl.etf.mr.today.utils.ItemTypeHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewSingleItemActivity extends AppCompatActivity {

    public static final int ItemNotFound = 1003;

    private List<Drawable> images;

    private RecyclerView imagesRecyclerView;
    private ImageAdapter adapter;
    TextView emptyPicturesTextView;

    private Item item;

    private List<String> picturesUrl;
    private ApplicationDb database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_item);

        item = (Item) getIntent().getSerializableExtra("selected");
        if(item == null){
            System.out.println("ITEM IS NULL!!!");
            setResult(ItemNotFound, "Lorem Ipsum");
        }

        // Text view
        emptyPicturesTextView = findViewById(R.id.picturesEmptyListTextView);

        // Set labels
        MaterialTextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(item.getTitle());

        Chip typeTextView = findViewById(R.id.typeChip);
        typeTextView.setText(ItemTypeHelper.getCode(item.getType(), this));

        MaterialTextView timestampTextView = findViewById(R.id.timestampTextView);
        timestampTextView.setText(DateTimeProvider.printDateTime(item.getTimestamp()));

        MaterialTextView descriptionTextView = findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(item.getDescription());

        // Set location button
        FloatingActionButton locationButton = findViewById(R.id.locationButton);
        if(item.getLocation() == null || item.getLocation().isBlank()){
            locationButton.setVisibility(View.GONE);
        }
        locationButton.setOnClickListener(view -> {
            startActivity(new Intent(this, MapsActivity.class).putExtra("location", item.getLocation()));
        });

        // Recycler view and list init
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        picturesUrl = new ArrayList<>();
        adapter = new ImageAdapter(picturesUrl);
        imagesRecyclerView.setAdapter(adapter);

        // Get pics
        retrievePictures();
    }

    private void setResult(int flag, String object){
        setResult(flag);
        finish();
    }

    private void retrievePictures() {
        imagesRecyclerView.setAdapter(adapter);
        database = ApplicationDb.getInstance(this);
        new ViewSingleItemActivity.GetPicturesForItemAsync(this, item).execute();
    }

    private static class GetPicturesForItemAsync extends AsyncTask<Void, Void, List<String>> {

        private WeakReference<ViewSingleItemActivity> ref;
        private Item item;

        GetPicturesForItemAsync(ViewSingleItemActivity ref, Item item) {
            this.ref = new WeakReference<>(ref);
            this.item = item;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            if (ref.get() != null){
                System.out.println("--> Get pics");
                return ref.get().database.picureDao()
                                         .getPicsForItem(item.getUuid())
                                         .stream()
                                         .map(picture -> picture.getUrl())
                                         .collect(Collectors.toList());
            }
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<String> items) {
            if (items != null && items.size() > 0) {
                System.out.println("Size: " + items.size());
                ref.get().picturesUrl.clear();
                ref.get().picturesUrl.addAll(items);
                // hides empty text view
                ref.get().emptyPicturesTextView.setVisibility(View.GONE);
                ref.get().imagesRecyclerView.setVisibility(View.VISIBLE);
                ref.get().adapter.notifyDataSetChanged();
            } else {
                ref.get().emptyPicturesTextView.setVisibility(View.VISIBLE);
                ref.get().imagesRecyclerView.setVisibility(View.GONE);
            }
        }
    }
}