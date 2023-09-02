package org.unibl.etf.mr.today.ui.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.unibl.etf.mr.today.BuildConfig;
import org.unibl.etf.mr.today.R;
import org.unibl.etf.mr.today.db.ApplicationDb;
import org.unibl.etf.mr.today.db.entities.Item;
import org.unibl.etf.mr.today.db.entities.Picture;
import org.unibl.etf.mr.today.utils.DateTimeProvider;
import org.unibl.etf.mr.today.utils.FileSystemManager;
import org.unibl.etf.mr.today.utils.ItemTypeHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddItemActivity extends AppCompatActivity {

    private final int GalleryReqCode = 1000;
    private final int CameraReqCode = 1001;
    private String uuid;

    TextInputEditText titleTextView;
    TextInputEditText descriptionTextView;
    TextInputEditText locationTextView;
    TextView dateTimeTextView;
    TextInputEditText netPicUrlTextView;

    Spinner itemTypePicker;

    int year, month, day, hour = 0, minute = 0, type = -1;
    String title, description, location;
    Date pickedDate;

    List<String> pictures = new ArrayList<>();

    private ApplicationDb database;
    private Item item;

    private String currentCameraPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Set UUID of new activity
        uuid = UUID.randomUUID().toString();

        // Text views
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        locationTextView = findViewById(R.id.locationTextView);
        dateTimeTextView = findViewById(R.id.timestampTextView);
        netPicUrlTextView = findViewById(R.id.netPictureTextView);

        // Spinner
        itemTypePicker = findViewById(R.id.typeComboBox);
        itemTypePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                LinearLayout picButtons = AddItemActivity.this.findViewById(R.id.picButtonsLayout);
                TextInputLayout urlLayout = AddItemActivity.this.findViewById(R.id.netPictureTextViewLayout);
                System.out.println("POSITION = " + position);
                if(position == 2){
                    System.out.println("FREE ACTIVITY SELECTED");
                    picButtons.setVisibility(View.VISIBLE);
                    urlLayout.setVisibility(View.VISIBLE);
                } else {
                    picButtons.setVisibility(View.GONE);
                    urlLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }

        });

        // Buttons
        FloatingActionButton datePickerButton = findViewById(R.id.datePickerButton);
        datePickerButton.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddItemActivity.this, (datePicker, i, i1, i2) -> {
                year = i;
                month = i1 + 1;
                day = i2;
                AddItemActivity.this.setDateTimeString();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        FloatingActionButton timePickerButton = findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddItemActivity.this, (timePicker, i, i1) -> {
                hour = i;
                minute = i1;
                AddItemActivity.this.setDateTimeString();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        FloatingActionButton galleryButton = findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GalleryReqCode);
        });

        FloatingActionButton takePictureButton = findViewById(R.id.takePhotoButton);
        takePictureButton.setOnClickListener(view -> {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = FileSystemManager.getInstance().createEmptyFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        currentCameraPhotoUrl = photoFile.getAbsolutePath();
                        Uri photoURI = FileProvider.getUriForFile(this,
                                                          BuildConfig.APPLICATION_ID + ".provider",
                                                                  photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CameraReqCode);
                    }
                }
            }
        });

        FloatingActionButton loadFromNetButton = findViewById(R.id.loadFromNetButton);
        loadFromNetButton.setOnClickListener(view -> {
            String picUrl = netPicUrlTextView.getText().toString();
            if(picUrl == null || picUrl.isBlank()){
                Toast toast = Toast.makeText(AddItemActivity.this,
                                             getString(R.string.pic_url_must_contain_value),
                                             Toast.LENGTH_LONG);
                toast.setMargin(75, 75);
                toast.show();
            } else if(!Patterns.WEB_URL.matcher(picUrl).matches()){
                Toast toast = Toast.makeText(AddItemActivity.this,
                                             getString(R.string.invalid_url),
                                             Toast.LENGTH_LONG);
                toast.setMargin(75, 75);
                toast.show();
            } else {
                // load image
                Picasso.get().load(picUrl);
                pictures.add(picUrl);
                Toast toast = Toast.makeText(AddItemActivity.this,
                        getString(R.string.photo_upload_success),
                        Toast.LENGTH_SHORT);
                toast.setMargin(75, 75);
                toast.show();
            }
        });

        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(view -> {
            if(checkInputData()){
                List<Picture> picsToInsert = new ArrayList<>();
                for(String picUrl : pictures){
                    Picture picture = new Picture(uuid, picUrl);
                    picsToInsert.add(picture);
                }
                item = new Item(uuid, title, type, description, pickedDate, location);
                new InsertAsync(AddItemActivity.this, item, picsToInsert).execute();
            } else {
                Toast toast = Toast.makeText(AddItemActivity.this,
                                             getString(R.string.form_not_valid),
                                             Toast.LENGTH_LONG);
                toast.setMargin(75, 75);
                toast.show();
            }
        });

        // Database
        database = ApplicationDb.getInstance(AddItemActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == GalleryReqCode && data != null){
                Uri selectedImage = data.getData();
                try {
                    String picFilepath = FileSystemManager.getInstance().saveImage(uuid, getContentResolver().openInputStream(selectedImage));
                    if(picFilepath != null){
                        String path = "file://" + picFilepath;
                        pictures.add(path);
                        Toast toast = Toast.makeText(AddItemActivity.this,
                                getString(R.string.photo_upload_success),
                                Toast.LENGTH_SHORT);
                        toast.setMargin(75, 75);
                        toast.show();
                    } else throw new Exception();
                } catch (Exception e) {
                    Toast toast = Toast.makeText(AddItemActivity.this,
                            getString(R.string.default_error_msg),
                            Toast.LENGTH_SHORT);
                    toast.setMargin(75, 75);
                    toast.show();
                }
            }

            if(requestCode == CameraReqCode && currentCameraPhotoUrl != null){
                pictures.add("file://" + currentCameraPhotoUrl);
                currentCameraPhotoUrl = null;
                Toast toast = Toast.makeText(AddItemActivity.this,
                        getString(R.string.photo_upload_success),
                        Toast.LENGTH_SHORT);
                toast.setMargin(75, 75);
                toast.show();
            }
        }
    }

    private boolean checkInputData() {
        title = titleTextView.getText().toString();
        description = descriptionTextView.getText().toString();
        location = locationTextView.getText().toString();
        type = itemTypePicker.getSelectedItemPosition();

        if(title == null || title.isBlank())
            return false;

        if(description == null || description.isBlank())
            return false;

        if(type < 0 || type >= ItemTypeHelper.COUNT)
            return false;

        if(type == 0 && (location == null || location.isBlank()))
            return false;

        if(pickedDate == null)
            return false;

        return true;
    }

    private void setDateTimeString() {
        pickedDate = Date.from(LocalDateTime.of(year, month, day, hour, minute, 0)
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant());
        dateTimeTextView.setText(DateTimeProvider.printDateTime(pickedDate));
    }

    private void setResult(Item item, int flag){
        setResult(flag, new Intent().putExtra("item", item));
        finish();
    }

    private static class InsertAsync extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<AddItemActivity> activityRef;
        private Item item;
        private List<Picture> pictures;

        InsertAsync(AddItemActivity context, Item item, List<Picture> pictures){
            this.activityRef = new WeakReference<>(context);
            this.item = item;
            this.pictures = pictures;
        }

        @Override // This runs on a worker thread.
        protected Boolean doInBackground(Void... objs) {
            // Insert pics
            if(!pictures.isEmpty()){
                for(Picture pic : pictures){
                    activityRef.get().database.picureDao().insertPic(pic);
                }
            }
            // Insert activity
            long id = activityRef.get().database.itemDao().insertItem(item);
            item.setId(id);
            Log.e("ID ", "DoInBackground: " + id);
            return true;
        }

        @Override // This runs on main thread.
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                activityRef.get().setResult(item, 1);
                activityRef.get().finish();
            }
        }
    }
}