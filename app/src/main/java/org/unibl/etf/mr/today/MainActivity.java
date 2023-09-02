package org.unibl.etf.mr.today;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import org.unibl.etf.mr.today.databinding.ActivityMainBinding;
import org.unibl.etf.mr.today.ui.activities.StartNotificationActivity;
import org.unibl.etf.mr.today.utils.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static boolean firstEntry = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.home_fragment, R.id.navigation_calendar, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String option = sharedPreferences.getString(getResources().getString(R.string.notifications_preference_key),
                                                    getResources().getString(R.string.notifications_preference_default)); // this should be default
        Constants.NotifType notifType = Constants.NotifType.values()[Integer.parseInt(option)];

        if(notifType != Constants.NotifType.OFF){
//            System.out.println("NOTIF TYPE = " + notifType.toString());
//            System.out.println("FIRST = " + firstEntry);
            if(firstEntry){
                showUpcomingActivities(notifType);
            }
        }

        setLocale(getBaseContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        firstEntry = false;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        firstEntry = false;
//    }

    private void showUpcomingActivities(Constants.NotifType type) {
        Calendar calendar = Calendar.getInstance();
        Date start = calendar.getTime();
        Date end;

        switch (type){
            case HOUR:
                calendar.add(Calendar.HOUR, 1);
                break;
            case DAY:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                break;
            case WEEK:
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                break;
        }

        end = calendar.getTime();

        startActivity(new Intent(this, StartNotificationActivity.class)
                                .putExtra("start", start)
                                .putExtra("end", end));
    }

    private void setLocale(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = sharedPreferences.getString("language", context.getResources().getString(R.string.language_preference_default));
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void attachBaseContext(Context base) {
        setLocale(base);
        super.attachBaseContext(base);
    }
}